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

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.mini2Dx.minibus.transmission.MessageTransmission;
import org.mini2Dx.minibus.transmission.MessageTransmissionPool;
import org.mini2Dx.minibus.transmission.SynchronizedQueue;

/**
 * Sends and receives {@link MessageData}s - base class for implementations.
 */
public abstract class MessageExchange {
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

	protected final List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>(1);
	
	protected final MessageBus messageBus;
	protected final MessageTransmissionPool messageTransmissionPool;
	protected final Queue<MessageTransmission> messageQueue;

	private final int id;

	/**
	 * Constructor
	 * 
	 * @param messageBus
	 *            The {@link MessageBus} that this {@link MessageExchange}
	 *            belongs to
	 * @param messageHandlers
	 *            The {@link MessageHandler} instances to notify when {@link MessageData}s
	 *            are received
	 */
	public MessageExchange(MessageBus messageBus, MessageHandler... messageHandlers) {
		id = ID_GENERATOR.incrementAndGet();

		this.messageBus = messageBus;
		this.messageTransmissionPool = messageBus.transmissionPool;

		if(MessageBus.USE_JAVA_UTIL_CONCURRENT) {
			messageQueue = new LinkedBlockingQueue<MessageTransmission>();
		} else {
			messageQueue = new SynchronizedQueue<MessageTransmission>();
		}
		
		for(MessageHandler messageHandler : messageHandlers) {
			this.messageHandlers.add(messageHandler);
		}
	}

	/**
	 * An overidable method for processing a {@link MessageTransmission} before
	 * queueing into this {@link MessageExchange}
	 * 
	 * @param messageTransmission
	 *            The {@link MessageTransmission} to process
	 * @return True if this {@link MessageTransmission} should be queued
	 */
	protected boolean preQueue(MessageTransmission messageTransmission) {
		return true;
	}

	/**
	 * An overidable method for processing a {@link MessageTransmission} after
	 * queueing into this {@link MessageExchange}. Note this method is only
	 * called if the {@link MessageTransmission} was successfully queued.
	 * 
	 * @param messageTransmission
	 *            The {@link MessageTransmission} to process
	 */
	protected void postQueue(MessageTransmission messageTransmission) {
	}

	/**
	 * Queues a {@link MessageTransmission} to be sent to the
	 * {@link MessageHandler}
	 * 
	 * @param messageTransmission
	 */
	void queue(MessageTransmission messageTransmission) {
		if (!preQueue(messageTransmission)) {
			return;
		}
		if (isImmediate()) {
			for(MessageHandler messageHandler : messageHandlers) {
				messageHandler.onMessageReceived(messageTransmission.getMessageType(), messageTransmission.getSource(),
						this, messageTransmission.getMessage());
			}
		} else {
			messageQueue.offer(messageTransmission);
		}
		postQueue(messageTransmission);
	}

	/**
	 * Broadcasts a message from this {@link MessageExchange} to all other
	 * {@link MessageExchange}s
	 * 
	 * @param messageType
	 *            The message type to broadcast
	 */
	public void broadcast(String messageType) {
		messageBus.broadcast(this, messageType);
	}

	/**
	 * Broadcasts a message with {@link MessageData} from this
	 * {@link MessageExchange} to all other {@link MessageExchange}s
	 * 
	 * @param messageType
	 *            The message type to broadcast
	 * @param messageData
	 *            The {@link MessageData} to broadcast
	 */
	public void broadcast(String messageType, MessageData messageData) {
		messageBus.broadcast(this, messageType, messageData);
	}

	/**
	 * Sends a message with from this {@link MessageExchange} to another
	 * 
	 * @param destination
	 *            The {@link MessageExchange} to send the {@link MessageData} to
	 * @param messageType
	 *            The message type
	 */
	public void sendTo(MessageExchange destination, String messageType) {
		messageBus.send(this, destination, messageType);
	}

	/**
	 * Sends a message with {@link MessageData} from this
	 * {@link MessageExchange} to another
	 * 
	 * @param destination
	 *            The {@link MessageExchange} to send the {@link MessageData} to
	 * @param messageType
	 *            The message type
	 * @param messageData
	 *            The {@link MessageData} to send
	 */
	public void sendTo(MessageExchange destination, String messageType, MessageData messageData) {
		messageBus.send(this, destination, messageType, messageData);
	}

	/**
	 * Flushes all {@link MessageData}s in the queue to
	 * {@link MessageHandler#onMessageReceived(String, MessageExchange, MessageExchange, MessageData)}
	 */
	protected void flush() {
		while (!messageQueue.isEmpty()) {
			MessageTransmission messageTransmission = messageQueue.poll();
			for(MessageHandler messageHandler : messageHandlers) {
				messageHandler.onMessageReceived(messageTransmission.getMessageType(), messageTransmission.getSource(),
						this, messageTransmission.getMessage());
			}
			messageTransmission.release();
		}
	}

	/**
	 * Updates this {@link MessageExchange}
	 * 
	 * @param delta
	 *            (in seconds) The timestep or amount of time that has elapsed
	 *            since the last frame
	 */
	public abstract void update(float delta);

	/**
	 * Returns if this consumer immediately processes {@link MessageData}s
	 * 
	 * @return True if this {@link MessageExchange} should process
	 *         {@link MessageData}s immediately as they are published
	 */
	public abstract boolean isImmediate();

	/**
	 * Disposes of this {@link MessageExchange} so that it can no longer be used
	 */
	public void dispose() {
		messageQueue.clear();
		messageBus.dispose(this);
	}

	/**
	 * Returns the unique identifier of this {@link MessageExchange}
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns if this {@link MessageExchange} is used for anonymous messages by
	 * the {@link MessageBus}
	 * 
	 * @return False by defaut
	 */
	public boolean isAnonymous() {
		return false;
	}
}
