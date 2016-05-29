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
 * Unit tests for {@link IntervalMessageExchange}
 */
public class IntervalMessageExchangeTest {
	private static final String MESSAGE_TYPE = "message";
	private static final float DELAY = 0.5f;

	private final MessageBus messageBus = new MessageBus();
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	private final MessageExchange exchange = messageBus.createIntervalExchange(messageHandler, DELAY);

	@After
	public void teardown() {
		exchange.dispose();
	}

	@Test
	public void testReceivesBroadcastMessagesByInterval() {
		Assert.assertEquals(false,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));

		messageBus.broadcast(MESSAGE_TYPE);
		Assert.assertEquals(false,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));

		for (float f = 0.1f; f < DELAY; f += 0.1f) {
			exchange.update(0.1f);
			Assert.assertEquals(false,
					messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		}

		exchange.update(0.1f);
		Assert.assertEquals(true,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
	}

	@Test
	public void testReceivesDirectMessagesByInterval() {
		Assert.assertEquals(false,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));

		messageBus.sendTo(exchange, MESSAGE_TYPE);
		Assert.assertEquals(false,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));

		for (float f = 0.1f; f < DELAY; f += 0.1f) {
			exchange.update(0.1f);
			Assert.assertEquals(false,
					messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
		}

		exchange.update(0.1f);
		Assert.assertEquals(true,
				messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(MESSAGE_TYPE));
	}
}
