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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Unit tests for {@link MessageHandlerChain}
 */
public class MessageHandlerChainTest {
	private static final String MESSAGE_TYPE = "message";
	
	private final MessageBus messageBus = new MessageBus();
	private final MessageHandlerChain messageHandlerChain = new MessageHandlerChain();
	private final MessageExchange messageExchange = messageBus.createImmediateExchange(messageHandlerChain);
	
	@After
	public void teardown() {
		messageExchange.dispose();
	}

	@Test
	public void testMessageHandlerChain() {
		DummyMessageHandler dummyMessageHandler1 = new DummyMessageHandler();
		DummyMessageHandler dummyMessageHandler2 = new DummyMessageHandler();
		
		messageHandlerChain.add(dummyMessageHandler1);
		messageBus.broadcast(MESSAGE_TYPE);
		
		Assert.assertEquals(1, dummyMessageHandler1.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		
		messageHandlerChain.add(dummyMessageHandler2);
		Assert.assertEquals(1, dummyMessageHandler1.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(0, dummyMessageHandler2.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		
		messageBus.broadcast(MESSAGE_TYPE);
		Assert.assertEquals(2, dummyMessageHandler1.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(1, dummyMessageHandler2.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
	}
}
