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
package org.mini2Dx.minibus.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.channel.Channel;
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
		afterInitialisation(this);
	}
	
	@Override
	public void afterInitialisation(MessageConsumer consumer) {
		messageHandler.afterInitialisation(consumer);
	}
	
	@Override
	public void onMessageReceived(String channel, Message message) {
		messageHandler.onMessageReceived(channel, message);
	}
	
	@Override
	public void subscribe(String channel) {
		if(subscriptions.containsKey(channel)) {
			return;
		}
		subscriptions.put(channel, messageBus.subscribe(this, channel));
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
	public void subscribeToDefaultChannel() {
		subscribe(Channel.DEFAULT_CHANNEL);
	}

	@Override
	public void unsubscribeFromDefaultChannel() {
		unsubscribe(Channel.DEFAULT_CHANNEL);
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
