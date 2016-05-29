/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 See AUTHORS file
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.mini2Dx.minibus;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mini2Dx.minibus.exchange.ConcurrentMessageExchange;
import org.mini2Dx.minibus.exchange.ImmediateMessageExchange;
import org.mini2Dx.minibus.exchange.IntervalMessageExchange;
import org.mini2Dx.minibus.exchange.OnUpdateMessageExchange;
import org.mini2Dx.minibus.transmission.MessageTransmission;
import org.mini2Dx.minibus.transmission.MessageTransmissionPool;

/**
 * A message bus to publishing {@link Message}s
 */
public class MessageBus {
	final List<MessageExchange> exchangers = new CopyOnWriteArrayList<MessageExchange>();
	final MessageTransmissionPool transmissionPool = new MessageTransmissionPool();

	private final MessageExchange anonymousExchange;

	/**
	 * Constructor
	 */
	public MessageBus() {
		anonymousExchange = new AnonymousMessageExchange(this);
	}

	/**
	 * Updates all {@link MessageExchange}s
	 * 
	 * @param delta
	 *            (in seconds) The timestep or amount of time that has elapsed
	 *            since the last frame
	 */
	public void update(float delta) {
		for (MessageExchange exchanger : exchangers) {
			exchanger.update(delta);
		}
		anonymousExchange.flush();
	}

	/**
	 * Creates a {@link ImmediateMessageExchange} that processes messages
	 * immediately when they are received
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageExchange}
	 * @return A new {@link ImmediateMessageExchange}
	 */
	public MessageExchange createImmediateExchange(MessageHandler messageHandler) {
		ImmediateMessageExchange result = new ImmediateMessageExchange(this, messageHandler);
		exchangers.add(result);
		return result;
	}

	/**
	 * Creates a {@link IntervalMessageExchange} that processes messages after a
	 * certain amount of time has elapsed.
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageExchange}
	 * @param interval
	 *            The interval between processing {@link Message}s (in seconds)
	 * @return A new {@link IntervalMessageExchange}
	 */
	public MessageExchange createIntervalExchange(MessageHandler messageHandler, float interval) {
		IntervalMessageExchange result = new IntervalMessageExchange(interval, this, messageHandler);
		exchangers.add(result);
		return result;
	}

	/**
	 * Creates a {@link OnUpdateMessageExchange} that processes messages when
	 * {@link MessageBus#update(float)} is called
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageExchange}
	 * @return A new {@link OnUpdateMessageExchange}
	 */
	public MessageExchange createOnUpdateExchange(MessageHandler messageHandler) {
		OnUpdateMessageExchange result = new OnUpdateMessageExchange(this, messageHandler);
		exchangers.add(result);
		return result;
	}

	/**
	 * Creates a {@link ConcurrentMessageExchange} that processes messages on
	 * its own {@link Thread}. The exchanger/thread can be stopped by calling
	 * {@link MessageExchange#dispose()}
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageConsumer}
	 * @return A new {@link ConcurrentMessageExchange} running on its own thread
	 */
	public MessageExchange createConcurrentExchange(MessageHandler messageHandler) {
		ConcurrentMessageExchange result = new ConcurrentMessageExchange(this, messageHandler);
		exchangers.add(result);
		return result;
	}

	/**
	 * Broadcasts a {@link Message} to all {@link MessageExchange}s from an
	 * anonymous source
	 * 
	 * @param message
	 *            The {@link Message} to be published
	 */
	public void broadcast(Message message) {
		broadcast(anonymousExchange, message);
	}

	/**
	 * Broadcasts a {@link Message} to all {@link MessageExchange}s from a
	 * specified {@link MessageExchange}
	 * 
	 * @param messageExchange
	 *            The {@link MessageExchange} to broadcast the {@link Message}
	 *            from
	 * @param message
	 *            The {@link Message} to broadcast
	 */
	public void broadcast(MessageExchange messageExchange, Message message) {
		if (exchangers.size() == 0) {
			return;
		}
		MessageTransmission messageTransmission = transmissionPool.allocate();
		messageTransmission.setMessage(message);
		messageTransmission.setSource(messageExchange);

		for (int i = exchangers.size() - 1; i >= 0; i--) {
			MessageExchange exchange = exchangers.get(i);
			if (exchange.getId() == messageExchange.getId()) {
				continue;
			}
			messageTransmission.allocate();
			exchange.queue(messageTransmission);
		}
	}

	/**
	 * Sends a {@link Message} from one {@link MessageExchange} to another
	 * 
	 * @param source
	 *            The {@link MessageExchange} the {@link Message} is sent from
	 * @param destination
	 *            The {@link MessageExchange} the {@link Message} is sent to
	 * @param message
	 *            The {@link Message} that is sent
	 */
	public void send(MessageExchange source, MessageExchange destination, Message message) {
		if (source == null) {
			throw new RuntimeException("source cannot be null, use sendTo() instead");
		}
		MessageTransmission messageTransmission = transmissionPool.allocate();
		messageTransmission.allocate();
		messageTransmission.setMessage(message);
		messageTransmission.setSource(source);
		destination.queue(messageTransmission);
	}

	/**
	 * Sends a {@link Message} to a {@link MessageExchange} from an anonymous
	 * source
	 * 
	 * @param destination
	 *            The {@link MessageExchange} the {@link Message} is sent to
	 * @param message
	 *            The {@link Message} that is sent
	 */
	public void sendTo(MessageExchange destination, Message message) {
		MessageTransmission messageTransmission = transmissionPool.allocate();
		messageTransmission.allocate();
		messageTransmission.setMessage(message);
		messageTransmission.setSource(anonymousExchange);
		destination.queue(messageTransmission);
	}

	/**
	 * Internal use only. Please use {@link MessageExchange#dispose()}
	 */
	public void dispose(MessageExchange messageExchange) {
		exchangers.remove(messageExchange);
	}

	/**
	 * Returns the id of the {@link MessageExchange} used for anonymous sending
	 * 
	 * @return
	 */
	public int getAnonymousExchangeId() {
		return anonymousExchange.getId();
	}

	/**
	 * An internal {@link MessageExchange} for anonymous message sending
	 */
	private class AnonymousMessageExchange extends MessageExchange {

		public AnonymousMessageExchange(MessageBus messageBus) {
			super(messageBus, new MessageHandler() {
				@Override
				public void onMessageReceived(MessageExchange source, MessageExchange receiver, Message message) {
				}
			});
		}

		@Override
		public void update(float delta) {
		}

		@Override
		public boolean isImmediate() {
			return true;
		}
	}
}
