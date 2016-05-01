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

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;

/**
 *
 */
public abstract class QueryMessageHandler extends TransactionMessageHandler {
	private String expectedChannel;
	private MessageHandlerChain messageHandlerChain;
	private float timer = 0f;
	private float timeout = MessageBus.DEFAULT_QUERY_TIMEOUT;
	private boolean finished = false;
	private Message beginMessage;
	
	public abstract void onMessageReceived(Message message);
	
	public abstract void onTimeout();

	@Override
	public void onMessageReceived(String channel, Message message) {
		if(expectedChannel == null) {
			return;
		}
		if(!expectedChannel.equals(channel)) {
			return;
		}
		if(message.getTransactionId() != beginMessage.getTransactionId()) {
			return;
		}
		switch(message.getTransactionState()) {
		case CONTINUE:
		case END:
			onMessageReceived(message);
			messageHandlerChain.unsubscribe(expectedChannel);
			messageHandlerChain.remove(this);
			finished = true;
			break;
		case BEGIN:
		case NOTIFY:
		default:
			break;
		}
	}

	public void initialise(String expectedChannel, Message beginMessage, float timeout, MessageHandlerChain messageHandlerChain) {
		this.beginMessage = beginMessage;
		this.messageHandlerChain = messageHandlerChain;
		this.timeout = timeout;
		this.expectedChannel = expectedChannel;
		
		messageHandlerChain.add(this);
		messageHandlerChain.subscribe(expectedChannel);
	}
	
	public boolean update(float delta) {
		timer += delta;
		return timer >= timeout;
	}
	
	public boolean isFinished() {
		return finished;
	}
}
