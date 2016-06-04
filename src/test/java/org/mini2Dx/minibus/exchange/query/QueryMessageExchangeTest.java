/**
 * Copyright 2016 Thomas Cashman
 */
package org.mini2Dx.minibus.exchange.query;

import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageData;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Unit tests for {@link QueryMessageExchange}
 */
public class QueryMessageExchangeTest implements MessageHandler {
	private static final String MESSAGE_TYPE = "message";
	private static final String BROADCAST_MESSAGE_TYPE = "broadcast";
	
	private final MessageBus messageBus = new MessageBus();
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	
	private MessageExchange queryExchange;
	private boolean respondToMessage = false;
	
	@Test
	public void testReceivesResponse() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = true;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, messageHandler);
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(MESSAGE_TYPE));
	}
	
	@Test
	public void testIgnoresBroadcasts() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = false;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, messageHandler);
		sourceExchange.broadcast(BROADCAST_MESSAGE_TYPE);
		sourceExchange.sendTo(queryExchange, MESSAGE_TYPE);
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(MESSAGE_TYPE));
	}
	
	@Test
	public void testIgnoresAnonymousMessages() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = false;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, messageHandler);
		messageBus.sendTo(queryExchange, BROADCAST_MESSAGE_TYPE);
		sourceExchange.sendTo(queryExchange, MESSAGE_TYPE);
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(MESSAGE_TYPE));
	}

	@Override
	public void onMessageReceived(String messageType, MessageExchange source, MessageExchange receiver,
			MessageData messageData) {
		queryExchange = source;
		if(respondToMessage) {
			receiver.sendTo(source, messageType);
		}
	}
}
