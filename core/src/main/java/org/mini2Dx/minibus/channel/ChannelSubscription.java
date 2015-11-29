/**
 * Copyright 2015 Thomas Cashman
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
