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
 * Integration tests for {@link QueryMessageExchange}
 */
public class QueryMessageExchangeTest implements MessageHandler {
	private static final String MESSAGE_TYPE = "message";
	private static final String RESPONSE_MESSAGE_TYPE = "response";
	private static final String INVALID_RESPONSE_MESSAGE_TYPE = "invalid";
	
	private final MessageBus messageBus = new MessageBus();
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	
	private MessageExchange queryExchange;
	private boolean respondToMessage = false;
	private boolean broadcastResponse = false;
	
	@Test
	public void testReceivesBroadcastResponse() {
		messageBus.createImmediateExchange(this);
		respondToMessage = true;
		broadcastResponse = true;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, RESPONSE_MESSAGE_TYPE, messageHandler);
		Assert.assertEquals(1, messageBus.getQueryMessagePoolSize());
		Assert.assertEquals(1, messageBus.getTotalActiveExchanges());
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(RESPONSE_MESSAGE_TYPE));
	}
	
	@Test
	public void testReceivesDirectResponse() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = true;
		broadcastResponse = false;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, RESPONSE_MESSAGE_TYPE, true, messageHandler);
		Assert.assertEquals(1, messageBus.getQueryMessagePoolSize());
		Assert.assertEquals(1, messageBus.getTotalActiveExchanges());
		
		Assert.assertEquals(1, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(true, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(RESPONSE_MESSAGE_TYPE));
	}
	
	@Test
	public void testIgnoresBroadcastResponseWhenDirectRequired() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = false;
		broadcastResponse = true;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, RESPONSE_MESSAGE_TYPE, true, messageHandler);
		
		Assert.assertEquals(2, messageBus.getTotalActiveExchanges());
		sourceExchange.broadcast(RESPONSE_MESSAGE_TYPE);
		Assert.assertEquals(2, messageBus.getTotalActiveExchanges());
		
		Assert.assertEquals(0, messageBus.getQueryMessagePoolSize());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(RESPONSE_MESSAGE_TYPE));
		Assert.assertEquals(0, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(messageBus.getAnonymousExchangeId()).contains(RESPONSE_MESSAGE_TYPE));
	}
	
	@Test
	public void testIgnoresInvalidResponses() {
		MessageExchange sourceExchange = messageBus.createImmediateExchange(this);
		respondToMessage = false;
		
		messageBus.broadcastQuery(MESSAGE_TYPE, RESPONSE_MESSAGE_TYPE, messageHandler);
		
		Assert.assertEquals(2, messageBus.getTotalActiveExchanges());
		messageBus.sendTo(queryExchange, INVALID_RESPONSE_MESSAGE_TYPE);
		Assert.assertEquals(2, messageBus.getTotalActiveExchanges());
		
		Assert.assertEquals(0, messageBus.getQueryMessagePoolSize());		
		Assert.assertEquals(0, messageHandler.getMessagesReceived(sourceExchange.getId()).size());
		Assert.assertEquals(false, messageHandler.getMessagesReceived(sourceExchange.getId()).contains(INVALID_RESPONSE_MESSAGE_TYPE));
	}

	@Override
	public void onMessageReceived(String messageType, MessageExchange source, MessageExchange receiver,
			MessageData messageData) {
		if(messageType.equals(RESPONSE_MESSAGE_TYPE)) {
			//Prevent loops
			return;
		}
		
		queryExchange = source;
		if(respondToMessage) {
			if(broadcastResponse) {
				messageBus.broadcast(RESPONSE_MESSAGE_TYPE);
			} else {
				receiver.sendTo(source, RESPONSE_MESSAGE_TYPE);
			}
		}
	}
}
