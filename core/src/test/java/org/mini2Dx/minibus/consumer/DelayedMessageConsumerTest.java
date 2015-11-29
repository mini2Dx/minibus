/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.consumer;

import org.junit.Test;
import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

import junit.framework.Assert;

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
