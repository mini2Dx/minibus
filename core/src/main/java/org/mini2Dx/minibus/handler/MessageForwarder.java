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
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;

/**
 * Common class for implementing a {@link MessageHandler} that receives
 * {@link Message}s on one {@link Channel} and forwards them to another.
 */
public abstract class MessageForwarder implements MessageHandler {
	private final String leftChannel, rightChannel;
	private MessageBus messageBus;

	/**
	 * Constructor
	 * 
	 * @param leftChannel
	 *            The {@link Channel} name to receive {@link Message}s from
	 * @param rightChannel
	 *            The {@link Channel} name to forward {@link Message}s to
	 */
	public MessageForwarder(String leftChannel, String rightChannel) {
		this.leftChannel = leftChannel;
		this.rightChannel = rightChannel;
	}

	@Override
	public void afterInitialisation(MessageBus messageBus, MessageConsumer consumer) {
		this.messageBus = messageBus;
		consumer.subscribe(leftChannel);
	}

	@Override
	public void onMessageReceived(String channel, Message message) {
		if (!forward(message)) {
			return;
		}
		messageBus.publish(rightChannel, message);
	}

	/**
	 * Called when a {@link Message} is received
	 * 
	 * @param message
	 *            The {@link Message} that was received
	 * @return True if the {@link Message} should be forwarded
	 */
	public abstract boolean forward(Message message);
}
