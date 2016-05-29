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
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Unit tests for {@link AllowAllMessageForwarder}
 */
public class AllowAllMessageForwarderTest {
	private final MessageBus messageBus = new MessageBus();
	private final DummyMessageHandler receivingMessageHandler = new DummyMessageHandler();
	private final MessageExchange receivingExchange = messageBus.createImmediateExchange(receivingMessageHandler);
	private final MessageForwarder messageForwarder = new AllowAllMessageForwarder(receivingExchange);
	private final MessageExchange forwardingExchange = messageBus.createImmediateExchange(messageForwarder);
	
	@After
	public void teardown() {
		receivingExchange.dispose();
		forwardingExchange.dispose();
	}
	
	@Test
	public void testMessageForwarding() {
		messageBus.sendTo(forwardingExchange, new DummyMessage(202));
		Assert.assertEquals(1, receivingMessageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
	}
}
