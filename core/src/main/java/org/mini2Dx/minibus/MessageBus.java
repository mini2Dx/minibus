/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mini2Dx.minibus.channel.Channel;
import org.mini2Dx.minibus.channel.ChannelSubscription;
import org.mini2Dx.minibus.consumer.DelayedMessageConsumer;
import org.mini2Dx.minibus.consumer.ImmediateMessageConsumer;
import org.mini2Dx.minibus.consumer.OnUpdateMessageConsumer;

/**
 *
 */
public class MessageBus {
	/**
	 * The default pool size for subscriptions per channel
	 */
	public static final int DEFAULT_POOL_SIZE = 10;

	private final int subscriptionPoolSize;
	private final ConcurrentMap<String, Channel> channels;
	
	/**
	 * Constructs a message bus with the {@link #DEFAULT_POOL_SIZE}
	 */
	public MessageBus() {
		this(DEFAULT_POOL_SIZE);
	}

	/**
	 * Constructs a message bus with a specific subscription pool size per channel
	 * @param subscriptionPoolSize
	 *            The amount of subscriptions expected per channel. Note that
	 *            larger values require more memory.
	 */
	public MessageBus(int subscriptionPoolSize) {
		this.subscriptionPoolSize = subscriptionPoolSize;

		channels = new ConcurrentHashMap<String, Channel>();
	}

	public MessageConsumer createImmediateConsumer(MessageHandler messageHandler) {
		return new ImmediateMessageConsumer(this, messageHandler);
	}

	public MessageConsumer createDelayedConsumer(MessageHandler messageHandler, float delay) {
		return new DelayedMessageConsumer(this, messageHandler, delay);
	}

	public MessageConsumer createOnUpdateConsumer(MessageHandler messageHandler) {
		return new OnUpdateMessageConsumer(this, messageHandler);
	}

	public void publish(String channel, Message message) {
		if (!channels.containsKey(channel)) {
			return;
		}
		channels.get(channel).publish(message);
	}

	public ChannelSubscription subscribe(MessageConsumer consumer, String channel) {
		if (!channels.containsKey(channel)) {
			channels.put(channel, new Channel(channel, subscriptionPoolSize));
		}
		return channels.get(channel).allocate(consumer);
	}
}
