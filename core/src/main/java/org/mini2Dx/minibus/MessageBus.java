/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
	public static final int DEFAULT_POOL_SIZE = 5;

	private final ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	private final List<MessageConsumer> consumers = new CopyOnWriteArrayList<MessageConsumer>();
	private final int subscriptionPoolSize;

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
	}

	/**
	 * Updates all {@link MessageConsumer}s. To update consumers individually,
	 * call {@link MessageConsumer#update(float)}.
	 * 
	 * @param delta (in seconds) The timestep or amount of time that has elapsed
	 *            since the last frame
	 */
	public void updateConsumers(float delta) {
		for (MessageConsumer consumer : consumers) {
			consumer.update(delta);
		}
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
		MessageConsumer result = new ImmediateMessageConsumer(this, messageHandler);
		consumers.add(result);
		return result;
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
		MessageConsumer result = new DelayedMessageConsumer(this, messageHandler, delay);
		consumers.add(result);
		return result;
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
		MessageConsumer result = new OnUpdateMessageConsumer(this, messageHandler);
		consumers.add(result);
		return result;
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

	/**
	 * Internal use only. Please use {@link MessageConsumer#dispose()}
	 */
	public void dispose(MessageConsumer messageConsumer) {
		consumers.remove(messageConsumer);
	}
}
