/**
 * Copyright 2015 Thomas Cashman
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
		for (int i = startIndex + 1; i < size; i++) {
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
