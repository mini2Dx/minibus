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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.handler.MessageHandlerChain;

/**
 * An object pool for {@link QueryMessageExchange} instances
 */
public class QueryMessageExchangePool {
	private final Queue<QueryMessageExchange> pool = new ConcurrentLinkedQueue<QueryMessageExchange>();
	
	private final MessageBus messageBus;
	
	public QueryMessageExchangePool(MessageBus messageBus) {
		this.messageBus = messageBus;
	}
	
	public QueryMessageExchange allocate(MessageHandler messageHandler) {
		QueryMessageExchange result = pool.poll();
		if(result == null) {
			result = new QueryMessageExchange(this, messageBus, new MessageHandlerChain());
		}
		result.setMessageHandler(messageHandler);
		return result;
	}
	
	public void release(QueryMessageExchange queryMessageExchange) {
		pool.offer(queryMessageExchange);
	}
	
	public int getSize() {
		return pool.size();
	}
}
