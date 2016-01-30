/**
 * Copyright (c) 2016 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of mini2Dx, minibus nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.minibus.consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Unit tests for {@link DelayedMessageConsumer}
 */
public class DelayedMessageConsumerTest {
	private static final float DELAY = 0.5f;
	private static final String CHANNEL_1 = "channel1";
	private static final String CHANNEL_2 = "channel2";
	
	private final MessageBus messageBus = new MessageBus(10);
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	private final MessageConsumer consumer = messageBus.createDelayedConsumer(messageHandler, DELAY);
	
	@After
	public void teardown() {
		consumer.dispose();
	}
	
	@Test
	public void testAfterInitialisation() {
		Assert.assertEquals(true, messageHandler.isAfterInitialisationCalled());
	}
	
	@Test
	public void testNoDuplicateSubscriptions() {
		Message message = new DummyMessage(1);
		consumer.subscribe(CHANNEL_1);
		consumer.subscribe(CHANNEL_1);
		
		messageBus.publish(CHANNEL_1, message);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		consumer.update(DELAY);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(1, messageHandler.getMessagesReceived(CHANNEL_1).size());
	}
	
	@Test
	public void testMessagesBySubscription() {
		Message message = new DummyMessage(1);
		consumer.subscribe(CHANNEL_1);
		
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		messageBus.publish(CHANNEL_1, message);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		consumer.update(DELAY);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		messageBus.publish(CHANNEL_2, message);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		consumer.update(DELAY);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(CHANNEL_2).size());
	}
	
	@Test
	public void testMessagesByChannel() {
		Message message = new DummyMessage(1);
		consumer.subscribe(CHANNEL_1);
		consumer.subscribe(CHANNEL_2);
		
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		messageBus.publish(CHANNEL_1, message);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		consumer.update(DELAY);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		messageBus.publish(CHANNEL_2, message);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		consumer.update(DELAY);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_2).contains(message));
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(CHANNEL_1).size());
		Assert.assertEquals(1, messageHandler.getMessagesReceived(CHANNEL_2).size());
	}
	
	@Test
	public void testMessagesByDelay() {
		Message message = new DummyMessage(1);
		consumer.subscribe(CHANNEL_1);
		
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		
		messageBus.publish(CHANNEL_1, message);
		Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		
		for(float f = 0.1f; f < DELAY; f += 0.1f) {
			consumer.update(0.1f);
			Assert.assertEquals(false, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
		}
		
		consumer.update(0.1f);
		Assert.assertEquals(true, messageHandler.getMessagesReceived(CHANNEL_1).contains(message));
	}
}
