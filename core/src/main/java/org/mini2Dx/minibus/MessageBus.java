/**
 * Copyright (c) 2016 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of mini2Dx, minibus nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.minibus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mini2Dx.minibus.channel.Channel;
import org.mini2Dx.minibus.channel.ChannelSubscription;
import org.mini2Dx.minibus.consumer.ConcurrentMessageConsumer;
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
	 * @param delta
	 *            (in seconds) The timestep or amount of time that has elapsed
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
	 * Creates a {@link ConcurrentMessageConsumer} that processes messages on
	 * its own {@link Thread}. The consumer/thread can be stopped by calling
	 * {@link MessageConsumer#dispose()}
	 * 
	 * @param messageHandler
	 *            The {@link MessageHandler} for processing messages received by
	 *            the {@link MessageConsumer}
	 * @return A new {@link ConcurrentMessageConsumer} running on its own thread
	 */
	public MessageConsumer createConcurrentConsumer(MessageHandler messageHandler) {
		MessageConsumer result = new ConcurrentMessageConsumer(this, messageHandler);
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
	 * Publishes a {@link Message} to the default channel
	 * 
	 * @param message
	 *            The {@link Message} to be published
	 */
	public void publish(Message message) {
		publish(Channel.DEFAULT_CHANNEL, message);
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
