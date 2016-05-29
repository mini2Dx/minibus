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
import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;
import org.mini2Dx.minibus.exchange.ImmediateMessageExchange;

/**
 * Unit tests for {@link ImmediateMessageExchange}
 */
public class ImmediateMessageExchangeTest {
	private final MessageBus messageBus = new MessageBus();
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	private final MessageExchange exchange = messageBus.createImmediateExchange(messageHandler);
	
	@After
	public void teardown() {
		exchange.dispose();
	}
	
	@Test
	public void testReceivesBroadcastMessages() {
		Message message = new DummyMessage(1);
		messageBus.broadcast(message);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(message));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(message));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
	}
	
	@Test
	public void testDoesNotReceiveOwnBroadcastMessages() {
		Message message = new DummyMessage(2);
		exchange.broadcast(message);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(message));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(message));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
	}
	
	@Test
	public void testReceivesDirectMessages() {
		Message message = new DummyMessage(1);
		messageBus.sendTo(exchange, message);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(message));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(exchange.getId()).contains(message));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(exchange.getId()).size());
		
		messageBus.send(exchange, exchange, message);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(message));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(exchange.getId()).contains(message));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(exchange.getId()).size());
	}
}
