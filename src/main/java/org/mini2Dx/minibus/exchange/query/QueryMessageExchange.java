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
package org.mini2Dx.minibus.exchange.query;

import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.exchange.ImmediateMessageExchange;
import org.mini2Dx.minibus.handler.MessageHandlerChain;
import org.mini2Dx.minibus.transmission.MessageTransmission;

/**
 * A {@link ImmediateMessageExchange} that immediately disposes of itself once a
 * response is received for a query
 */
public class QueryMessageExchange extends ImmediateMessageExchange {
	private final QueryMessageExchangePool exchangePool;
	private final MessageHandlerChain handlerChain;

	public QueryMessageExchange(QueryMessageExchangePool exchangePool, MessageBus messageBus,
			MessageHandlerChain handlerChain) {
		super(messageBus, handlerChain);
		this.exchangePool = exchangePool;
		this.handlerChain = handlerChain;
	}

	@Override
	protected boolean preQueue(MessageTransmission messageTransmission) {
		if (messageTransmission.getSource().isAnonymous()) {
			return false;
		}
		if (messageTransmission.isBroadcastMessage()) {
			return false;
		}
		return true;
	}

	@Override
	protected void postQueue(MessageTransmission messageTransmission) {
		dispose();
	}

	@Override
	public void dispose() {
		handlerChain.clear();
		messageQueue.clear();
		exchangePool.release(this);
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		handlerChain.add(messageHandler);
	}
}
