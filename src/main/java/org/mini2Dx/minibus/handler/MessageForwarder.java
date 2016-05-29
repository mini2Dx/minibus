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
package org.mini2Dx.minibus.handler;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.MessageHandler;

/**
 * Common class for implementing a {@link MessageHandler} that receives
 * {@link Message}s and forwards them to one or more {@link MessageExchange}s.
 */
public abstract class MessageForwarder implements MessageHandler {
	private final MessageExchange[] receivers;

	/**
	 * Constructor
	 * 
	 * @param receivers
	 *            The {@link MessageExchange}s to forward {@link Message}s to
	 */
	public MessageForwarder(MessageExchange... receivers) {
		if (receivers.length == 0) {
			throw new RuntimeException(MessageForwarder.class.getSimpleName() + " must have at least 1 "
					+ MessageExchange.class.getSimpleName() + " to forward messages to");
		}
		this.receivers = receivers;
	}

	@Override
	public void onMessageReceived(MessageExchange source, MessageExchange receiver, Message message) {
		if (!forward(message)) {
			return;
		}
		for (MessageExchange exchanger : receivers) {
			source.sendTo(exchanger, message);
		}
	}

	/**
	 * Called when a {@link Message} is received
	 * 
	 * @param message
	 *            The {@link Message} that was received
	 * @return True if the {@link Message} should be forwarded
	 */
	public abstract boolean forward(Message message);
}
