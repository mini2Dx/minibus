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
 * Consumers {@link Message}s after a certain amount of time has elapsed
 */
public class DelayedMessageConsumer extends BaseMessageConsumer implements MessageConsumer {
	private final float delay;

	private float timer;

	public DelayedMessageConsumer(MessageBus messageBus, MessageHandler messageHandler, float delay) {
		super(messageBus, messageHandler);
		this.delay = delay;
	}

	@Override
	public void update(float delta) {
		timer += delta;
		if(timer >= delay) {
			timer -= delay;
			
			for(ChannelSubscription subscription : subscriptions.values()) {
				subscription.flush();
			}
		}
	}
	
	@Override
	public boolean isImmediate() {
		return false;
	}
}
