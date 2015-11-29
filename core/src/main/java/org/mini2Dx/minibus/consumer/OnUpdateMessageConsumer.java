/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.consumer;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.channel.ChannelSubscription;

/**
 * Consumes {@link Message}s when {@link #update(float)} is called
 */
public class OnUpdateMessageConsumer extends BaseMessageConsumer implements MessageConsumer {
	
	public OnUpdateMessageConsumer(MessageBus messageBus, MessageHandler messageHandler) {
		super(messageBus, messageHandler);
	}

	@Override
	public void update(float delta) {
		for(ChannelSubscription subscription : subscriptions.values()) {
			subscription.flush();
		}
	}

	@Override
	public boolean isImmediate() {
		return false;
	}
}
