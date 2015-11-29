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
 * A message bus to publishing {@link Message}s
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
	 * Constructs a message bus with a specific subscription pool size per
	 * channel
	 * 
	 * @param subscriptionPoolSize
	 *            The amount of subscriptions expected per channel. Note that
	 *            larger values require more memory.
	 */
	public MessageBus(int subscriptionPoolSize) {
		this.subscriptionPoolSize = subscriptionPoolSize;

		channels = new ConcurrentHashMap<String, Channel>();
	}

	/**
	 * Creates a {@link ImmediateMessageConsumer} that processes messages
	 * immediately when they are published
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageConsumer}
	 * @return A new {@link ImmediateMessageConsumer}
	 */
	public MessageConsumer createImmediateConsumer(MessageHandler messageHandler) {
		return new ImmediateMessageConsumer(this, messageHandler);
	}

	/**
	 * Creates a {@link DelayedMessageConsumer} that processes messages after a
	 * certain amount of time has elapsed.
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageConsumer}
	 * @param delay
	 *            The amount of time to wait (in seconds) before processing
	 *            {@link Message}s
	 * @return A new {@link DelayedMessageConsumer}
	 */
	public MessageConsumer createDelayedConsumer(MessageHandler messageHandler, float delay) {
		return new DelayedMessageConsumer(this, messageHandler, delay);
	}

	/**
	 * Creates a {@link OnUpdateMessageConsumer} that processes messages when
	 * {@link MessageConsumer#update(float)} is called
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageConsumer}
	 * @return A new {@link OnUpdateMessageConsumer}
	 */
	public MessageConsumer createOnUpdateConsumer(MessageHandler messageHandler) {
		return new OnUpdateMessageConsumer(this, messageHandler);
	}

	/**
	 * Publishes a {@link Message} to this {@link MessageBus}
	 * 
	 * @param channel
	 *            The message channel to publish on. Only
	 *            {@link MessageConsumer}s subscribed to the channel will
	 *            receive the {@link Message}
	 * @param message
	 *            The {@link Message} to be published
	 */
	public void publish(String channel, Message message) {
		if (!channels.containsKey(channel)) {
			return;
		}
		channels.get(channel).publish(message);
	}

	/**
	 * Internal use only. Please use {@link MessageConsumer#subscribe(String)}
	 */
	public ChannelSubscription subscribe(MessageConsumer consumer, String channel) {
		if (!channels.containsKey(channel)) {
			channels.put(channel, new Channel(channel, subscriptionPoolSize));
		}
		return channels.get(channel).allocate(consumer);
	}
}
