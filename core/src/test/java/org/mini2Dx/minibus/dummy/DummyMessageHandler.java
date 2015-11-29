/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;

/**
 * A dummy {@link MessageHandler} for unit tests
 */
public class DummyMessageHandler implements MessageHandler {
	private final Map<String, List<Message>> messagesReceived = new HashMap<String, List<Message>>();
	private boolean afterInitialisationCalled = false;
	
	@Override
	public void afterInitialisation(MessageConsumer consumer) {
		afterInitialisationCalled = true;
	}
	
	@Override
	public void onMessageReceived(String channel, Message message) {
		if(!messagesReceived.containsKey(channel)) {
			messagesReceived.put(channel, new ArrayList<Message>());
		}
		messagesReceived.get(channel).add(message);
	}

	public List<Message> getMessagesReceived(String channel) {
		if(!messagesReceived.containsKey(channel)) {
			messagesReceived.put(channel, new ArrayList<Message>());
		}
		return messagesReceived.get(channel);
	}

	public boolean isAfterInitialisationCalled() {
		return afterInitialisationCalled;
	}
}
