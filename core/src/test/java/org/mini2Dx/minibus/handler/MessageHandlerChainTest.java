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

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;

/**
 * Unit tests for {@link MessageHandlerChain}
 */
public class MessageHandlerChainTest {
	private static final String CHANNEL = "channel1";
	
	private final MessageBus messageBus = new MessageBus(10);
	private final MessageHandlerChain messageHandlerChain = new MessageHandlerChain();
	private final MessageConsumer messageConsumer = messageBus.createImmediateConsumer(messageHandlerChain);
	
	@After
	public void teardown() {
		messageConsumer.dispose();
	}

	@Test
	public void testMessageHandlerChain() {
		DummyMessageHandler dummyMessageHandler1 = new DummyMessageHandler();
		DummyMessageHandler dummyMessageHandler2 = new DummyMessageHandler();
		
		messageHandlerChain.add(dummyMessageHandler1);
		messageHandlerChain.subscribe(CHANNEL);
		messageBus.publish(CHANNEL, new DummyMessage(7));
		
		Assert.assertEquals(1, dummyMessageHandler1.getMessagesReceived(CHANNEL).size());
		
		messageHandlerChain.add(dummyMessageHandler2);
		Assert.assertEquals(1, dummyMessageHandler1.getMessagesReceived(CHANNEL).size());
		Assert.assertEquals(0, dummyMessageHandler2.getMessagesReceived(CHANNEL).size());
		
		messageBus.publish(CHANNEL, new DummyMessage(8));
		Assert.assertEquals(2, dummyMessageHandler1.getMessagesReceived(CHANNEL).size());
		Assert.assertEquals(1, dummyMessageHandler2.getMessagesReceived(CHANNEL).size());
	}
}
