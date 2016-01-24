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
package org.mini2Dx.minibus.channel;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageConsumer;

/**
 * A {@link MessageConsumer} subscription to a {@link Channel}
 */
public class ChannelSubscription {
	private final Queue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	
	private final int index;
	private final Channel channel;
	
	private MessageConsumer consumer;
	
	public ChannelSubscription(int index, Channel channel) {
		this.index = index;
		this.channel = channel;
	}
	
	public void queue(Message message) {
		if(consumer.isImmediate()) {
			consumer.onMessageReceived(channel.getName(), message);
		} else {
			messageQueue.offer(message);
		}
	}
	
	public void allocate(MessageConsumer consumer) {
		this.consumer = consumer;
	}
	
	public void release() {
		channel.release(index);
		messageQueue.clear();
		consumer = null;
	}
	
	public void flush() {
		if(consumer == null) {
			return;
		}
		if(consumer.isImmediate()) {
			return;
		}
		while(!messageQueue.isEmpty()) {
			consumer.onMessageReceived(channel.getName(), messageQueue.poll());
		}
	}
}
