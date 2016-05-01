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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;
import org.mini2Dx.minibus.TransactionState;
import org.mini2Dx.minibus.dummy.DummyMessage;

/**
 * Integration test for {@link QueryMessageHandler} and {@link MessageBus}
 */
public class QueryMessageHandlerTest implements MessageHandler {
	private static final String CHANNEL = "channel";

	private final MessageBus messageBus = new MessageBus();

	@Test
	public void testQuery() {
		final int value = 7;
		final AtomicBoolean result = new AtomicBoolean(false);
		
		MessageConsumer messageConsumer = messageBus.createImmediateConsumer(this);
		messageBus.query(CHANNEL, new DummyMessage(TransactionState.BEGIN, value), new QueryMessageHandler() {
			@Override
			public void onMessageReceived(Message message) {
				DummyMessage dummyMessage = (DummyMessage) message;
				Assert.assertEquals(value, dummyMessage.getValue());
				result.set(true);
			}
			
			@Override
			public void onTimeout() {
				Assert.fail();
			}
		});
		messageBus.updateConsumers(1.0f);
		messageConsumer.dispose();
		Assert.assertEquals(true, result.get());
	}

	@Test
	public void testQueryWithTimeout() {
		final int value = 8;
		final AtomicBoolean result = new AtomicBoolean(false);
		messageBus.query(CHANNEL, new DummyMessage(TransactionState.BEGIN, value), new QueryMessageHandler() {
			@Override
			public void onMessageReceived(Message message) {
				Assert.fail();
			}
			
			@Override
			public void onTimeout() {
				result.set(true);
			}
		});
		for(float i = 0f; i < MessageBus.DEFAULT_QUERY_TIMEOUT; i++) {
			messageBus.updateConsumers(1.0f);
		}
		Assert.assertEquals(true, result.get());
	}

	@Test
	public void testQueryImmediate() {
		final int value = 9;
		final AtomicBoolean result = new AtomicBoolean(false);
		
		MessageConsumer messageConsumer = messageBus.createImmediateConsumer(this);
		messageBus.queryImmediate(CHANNEL, new DummyMessage(TransactionState.BEGIN, value), new QueryMessageHandler() {
			@Override
			public void onMessageReceived(Message message) {
				DummyMessage dummyMessage = (DummyMessage) message;
				Assert.assertEquals(value, dummyMessage.getValue());
				result.set(true);
			}
			
			@Override
			public void onTimeout() {
				Assert.fail();
			}
		});
		messageBus.updateConsumers(1.0f);
		messageConsumer.dispose();
		Assert.assertEquals(true, result.get());
	}

	@Test
	public void testQueryImmediateWithTimeout() {
		final int value = 10;
		final AtomicBoolean result = new AtomicBoolean(false);
		messageBus.queryImmediate(CHANNEL, new DummyMessage(TransactionState.BEGIN, value), new QueryMessageHandler() {
			@Override
			public void onMessageReceived(Message message) {
				Assert.fail();
			}
			
			@Override
			public void onTimeout() {
				result.set(true);
			}
		});
		for(float i = 0f; i < MessageBus.DEFAULT_QUERY_TIMEOUT; i++) {
			messageBus.updateConsumers(1.0f);
		}
		Assert.assertEquals(true, result.get());
	}

	@Override
	public void afterInitialisation(MessageBus messageBus, MessageConsumer consumer) {
		consumer.subscribe(CHANNEL);
	}

	@Override
	public void onMessageReceived(String channel, Message message) {
		if(message.getTransactionState() != TransactionState.END) {
			DummyMessage dummyMessage = (DummyMessage) message;
			messageBus.publish(channel,
					new DummyMessage(message.getTransactionId(), TransactionState.END, dummyMessage.getValue()));
		}
	}
}
