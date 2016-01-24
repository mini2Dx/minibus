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

import java.util.BitSet;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;

/**
 * A {@link Message} channel on the {@link MessageBus}
 */
public class Channel {
	public static final String DEFAULT_CHANNEL = "default";
	
	private final String name;
	private final int size;
	private final ChannelSubscription[] subscriptions;
	private final BitSet allocated;

	private int lastAllocatedIndex;

	public Channel(String name, int size) {
		this.name = name;
		this.size = size;

		subscriptions = new ChannelSubscription[size];
		for (int i = 0; i < size; i++) {
			subscriptions[i] = new ChannelSubscription(i, this);
		}

		allocated = new BitSet(size);
	}

	public void publish(Message message) {
		for (int i = 0; i < size; i++) {
			if (allocated.get(i)) {
				subscriptions[i].queue(message);
			}
		}
	}

	public ChannelSubscription allocate(MessageConsumer consumer) {
		int startIndex = lastAllocatedIndex;
		for (int i = startIndex; i < size; i++) {
			if (!allocated.get(i)) {
				lastAllocatedIndex = i;
				allocated.set(i);
				ChannelSubscription result = subscriptions[i];
				result.allocate(consumer);
				return result;
			}
		}
		for (int i = 0; i < startIndex; i++) {
			if (!allocated.get(i)) {
				lastAllocatedIndex = i;
				allocated.set(i);
				ChannelSubscription result = subscriptions[i];
				result.allocate(consumer);
				return result;
			}
		}
		throw new RuntimeException(
				"No channel subscriptions available in subscription pool. Please create a MessageBus with a larger pool size.");
	}

	public void release(int index) {
		allocated.clear(index);
	}

	public String getName() {
		return name;
	}
}
