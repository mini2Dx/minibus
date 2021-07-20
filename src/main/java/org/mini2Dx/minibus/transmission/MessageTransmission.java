/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 See AUTHORS file
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.mini2Dx.minibus.transmission;

import java.util.concurrent.atomic.AtomicInteger;

import org.mini2Dx.minibus.MessageData;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.pool.PooledMessageData;

/**
 *
 */
public class MessageTransmission {
	private final MessageTransmissionPool transmissionPool;
	private final AtomicInteger allocations = new AtomicInteger(0);
	
	private boolean broadcastMessage;
	private MessageExchange source;
	private String messageType;
	private MessageData messageData;
	
	public MessageTransmission(MessageTransmissionPool transmissionPool) {
		this.transmissionPool = transmissionPool;
	}
	
	public void allocate() {
		allocations.incrementAndGet();
	}
	
	public void release() {
		if(allocations.decrementAndGet() <= 0) {
			if(messageData instanceof PooledMessageData) {
				((PooledMessageData) messageData).release();
			}
			allocations.set(0);
			messageType = "";
			messageData = null;
			transmissionPool.release(this);
		}
	}

	public MessageExchange getSource() {
		return source;
	}

	public MessageData getMessage() {
		return messageData;
	}
	
	public void setSource(MessageExchange source) {
		this.source = source;
	}

	public void setMessageData(MessageData messageData) {
		this.messageData = messageData;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public boolean isBroadcastMessage() {
		return broadcastMessage;
	}

	public void setBroadcastMessage(boolean broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}
}
