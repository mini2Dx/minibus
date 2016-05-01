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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.util.ConcurrentMessage;

/**
 * Processes {@link Message}s on its own {@link Thread}. This thread will not
 * consume CPU if no messages are available. The consumer/thread can be stopped
 * by calling {@link ConcurrentMessageConsumer#dispose()}
 */
public class ConcurrentMessageConsumer extends BaseMessageConsumer implements Runnable {
	private final BlockingQueue<ConcurrentMessage> messageQueue = new LinkedBlockingQueue<ConcurrentMessage>();
	private final AtomicBoolean running = new AtomicBoolean(true);

	public ConcurrentMessageConsumer(MessageBus messageBus, MessageHandler messageHandler) {
		super(messageBus, messageHandler);
		new Thread(this).start();
	}

	@Override
	public void onMessageReceived(String channel, Message message) {
		try {
			messageQueue.put(new ConcurrentMessage(channel, message));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public boolean isImmediate() {
		return true;
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				ConcurrentMessage concurrentMessage = messageQueue.take();
				if(concurrentMessage.getChannel() == null) {
					return;
				}
				messageHandler.onMessageReceived(concurrentMessage.getChannel(), concurrentMessage.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose() {
		running.set(false);
		try {
			//Use null to signal thread to stop
			messageQueue.put(new ConcurrentMessage(null, null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}