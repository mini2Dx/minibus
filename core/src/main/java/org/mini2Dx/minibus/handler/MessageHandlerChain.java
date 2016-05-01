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
package org.mini2Dx.minibus.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.channel.Channel;

/**
 * A {@link MessageHandler} that allows multiple {@link MessageHandler}s to be
 * chained and receive the same {@link Message}s
 */
public class MessageHandlerChain implements MessageHandler, MessageConsumer {
	private final List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>(1);
	private final ReadWriteLock countLock = new ReentrantReadWriteLock();
	private final Map<String, AtomicInteger> subscriptionCounts = new HashMap<String, AtomicInteger>();
	
	private MessageBus messageBus;
	private MessageConsumer consumer;

	@Override
	public void afterInitialisation(MessageBus messageBus, MessageConsumer consumer) {
		this.messageBus = messageBus;
		this.consumer = consumer;
		
		for(String channel : subscriptionCounts.keySet()) {
			if(subscriptionCounts.get(channel).get() == 0) {
				continue;
			}
			consumer.subscribe(channel);
		}
		
		for(MessageHandler messageHandler : messageHandlers) {
			messageHandler.afterInitialisation(messageBus, this);
		}
	}

	@Override
	public void onMessageReceived(String channel, Message message) {
		for (int i = messageHandlers.size() - 1; i >= 0; i--) {
			messageHandlers.get(i).onMessageReceived(channel, message);
		}
	}

	public void add(MessageHandler messageHandler) {
		messageHandlers.add(messageHandler);
		if(messageBus == null) {
			return;
		}
		messageHandler.afterInitialisation(messageBus, this);
	}

	public void remove(MessageHandler messageHandler) {
		messageHandlers.remove(messageHandler);
	}

	public void clear() {
		messageHandlers.clear();
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void subscribe(String channel) {
		countLock.readLock().lock();
		if(!subscriptionCounts.containsKey(channel)) {
			countLock.readLock().unlock();
			countLock.writeLock().lock();
			subscriptionCounts.put(channel, new AtomicInteger(0));
			countLock.writeLock().unlock();
			countLock.readLock().lock();
		}
		int totalSubscriptions = subscriptionCounts.get(channel).incrementAndGet();
		countLock.readLock().unlock();
		if (consumer == null) {
			return;
		}
		if (totalSubscriptions > 1) {
			return;
		}
		consumer.subscribe(channel);
	}

	@Override
	public void subscribeToDefaultChannel() {
		subscribe(Channel.DEFAULT_CHANNEL);
	}

	@Override
	public void unsubscribe(String channel) {
		countLock.readLock().lock();
		if(!subscriptionCounts.containsKey(channel)) {
			countLock.readLock().unlock();
			return;
		}
		int totalSubscriptions = subscriptionCounts.get(channel).decrementAndGet();
		countLock.readLock().unlock();
		if (consumer == null) {
			return;
		}
		if (totalSubscriptions > 0) {
			return;
		}
		consumer.unsubscribe(channel);
	}

	@Override
	public void unsubscribeFromDefaultChannel() {
		unsubscribe(Channel.DEFAULT_CHANNEL);
	}

	@Override
	public boolean isImmediate() {
		if (consumer == null) {
			return false;
		}
		return consumer.isImmediate();
	}

	@Override
	public void dispose() {
		messageHandlers.clear();
		if (consumer == null) {
			return;
		}
		consumer.dispose();
		consumer = null;
	}

}
