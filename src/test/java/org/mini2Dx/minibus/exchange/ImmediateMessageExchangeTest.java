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
package org.mini2Dx.minibus.exchange;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Integration tests for {@link ImmediateMessageExchange}
 */
public abstract class ImmediateMessageExchangeTest {
	private static final String MESSAGE_TYPE = "message";
	
	private final MessageBus messageBus;
	private final DummyMessageHandler messageHandler;
	private final MessageExchange exchange;

	public ImmediateMessageExchangeTest(boolean useJavaUtilConcurrent) {
		MessageBus.USE_JAVA_UTIL_CONCURRENT = useJavaUtilConcurrent;

		messageBus = new MessageBus();
		messageHandler = new DummyMessageHandler();
		exchange = messageBus.createImmediateExchange(messageHandler);
	}
	
	@After
	public void teardown() {
		exchange.dispose();
	}
	
	@Test
	public void testReceivesBroadcastMessages() {
		messageBus.broadcast(MESSAGE_TYPE);
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(MESSAGE_TYPE));
	}
	
	@Test
	public void testDoesNotReceiveOwnBroadcastMessages() {
		exchange.broadcast(MESSAGE_TYPE);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
	}
	
	@Test
	public void testReceivesDirectMessages() {
		messageBus.sendTo(exchange, MESSAGE_TYPE);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
		
		messageBus.send(exchange, exchange, MESSAGE_TYPE);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(exchange.getId()).contains(MESSAGE_TYPE));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(exchange.getId()).size());
	}
}
