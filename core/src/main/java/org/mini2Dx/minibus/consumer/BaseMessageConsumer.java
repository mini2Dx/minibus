/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.channel.ChannelSubscription;

/**
 * Base class for all {@link MessageConsumer} implementations
 */
public abstract class BaseMessageConsumer implements MessageConsumer {
	protected final ConcurrentMap<String, ChannelSubscription> subscriptions = new ConcurrentHashMap<String, ChannelSubscription>();

	protected final MessageBus messageBus;
	protected final MessageHandler messageHandler;
	
	public BaseMessageConsumer(MessageBus messageBus, MessageHandler messageHandler) {
		this.messageBus = messageBus;
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void onMessageReceived(String channel, Message message) {
		messageHandler.onMessageReceived(channel, message);
	}
	
	@Override
	public void subscribe(String channel) {
		subscriptions.putIfAbsent(channel, messageBus.subscribe(this, channel));
	}

	@Override
	public void unsubscribe(String channel) {
		ChannelSubscription subscription = subscriptions.remove(channel);
		if(subscription == null) {
			return;
		}
		subscription.release();
	}
	
	@Override
	public void dispose() {
		for(ChannelSubscription subscription : subscriptions.values()) {
			subscription.release();
		}
		subscriptions.clear();
		messageBus.dispose(this);
	}
}
