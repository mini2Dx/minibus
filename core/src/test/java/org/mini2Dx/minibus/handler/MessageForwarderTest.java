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
import org.junit.Before;
import org.junit.Test;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.dummy.DummyMessage;
import org.mini2Dx.minibus.dummy.DummyMessageForwarder;
import org.mini2Dx.minibus.dummy.DummyMessageHandler;


/**
 * Unit tests for {@link MessageForwarder}
 */
public class MessageForwarderTest {
	private static final String LEFT_CHANNEL_1 = "channel1";
	private static final String LEFT_CHANNEL_2 = "channel2";
	private static final String RIGHT_CHANNEL_1 = "channel3";
	private static final String RIGHT_CHANNEL_2 = "channel4";
	
	private final MessageBus messageBus = new MessageBus(10);
	private final DummyMessageHandler messageHandler = new DummyMessageHandler();
	private final MessageConsumer receivingConsumer = messageBus.createImmediateConsumer(messageHandler);
	
	private MessageConsumer forwardingConsumer;
	
	@Before
	public void setUp() {
		receivingConsumer.subscribe(RIGHT_CHANNEL_1);
		receivingConsumer.subscribe(RIGHT_CHANNEL_2);
	}
	
	@After
	public void teardown() {
		forwardingConsumer.dispose();
	}
	
	@Test
	public void testAllowMessagesSingleChannel() {
		DummyMessageForwarder messageForwarder = new DummyMessageForwarder(LEFT_CHANNEL_1, RIGHT_CHANNEL_1);
		messageForwarder.setForwardMessages(true);
		forwardingConsumer = messageBus.createImmediateConsumer(messageForwarder);
		
		messageBus.publish(LEFT_CHANNEL_1, new DummyMessage(202));
		Assert.assertEquals(1, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(1, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(1, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
	}
	
	@Test
	public void testDropMessagesSingleChannel() {
		DummyMessageForwarder messageForwarder = new DummyMessageForwarder(LEFT_CHANNEL_1, RIGHT_CHANNEL_1);
		messageForwarder.setForwardMessages(false);
		forwardingConsumer = messageBus.createImmediateConsumer(messageForwarder);
		
		messageBus.publish(LEFT_CHANNEL_1, new DummyMessage(202));
		Assert.assertEquals(1, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(0, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
	}
	
	@Test
	public void testAllowMessagesMultiChannel() {
		DummyMessageForwarder messageForwarder = new DummyMessageForwarder(new String [] { LEFT_CHANNEL_1, LEFT_CHANNEL_2 }, new String [] { RIGHT_CHANNEL_1, RIGHT_CHANNEL_2 });
		messageForwarder.setForwardMessages(true);
		forwardingConsumer = messageBus.createImmediateConsumer(messageForwarder);
		
		messageBus.publish(LEFT_CHANNEL_1, new DummyMessage(202));
		Assert.assertEquals(1, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(1, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_2).size());
		Assert.assertEquals(1, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
		Assert.assertEquals(1, messageHandler.getMessagesReceived(RIGHT_CHANNEL_2).size());
		
		messageBus.publish(LEFT_CHANNEL_2, new DummyMessage(202));
		Assert.assertEquals(2, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(2, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_2).size());
		Assert.assertEquals(2, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
		Assert.assertEquals(2, messageHandler.getMessagesReceived(RIGHT_CHANNEL_2).size());
	}
	
	@Test
	public void testDropMessagesMultiChannel() {
		DummyMessageForwarder messageForwarder = new DummyMessageForwarder(new String [] { LEFT_CHANNEL_1, LEFT_CHANNEL_2 }, new String [] { RIGHT_CHANNEL_1, RIGHT_CHANNEL_2 });
		messageForwarder.setForwardMessages(false);
		forwardingConsumer = messageBus.createImmediateConsumer(messageForwarder);
		
		messageBus.publish(LEFT_CHANNEL_1, new DummyMessage(202));
		Assert.assertEquals(1, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(0, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_2).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(RIGHT_CHANNEL_2).size());
		
		messageBus.publish(LEFT_CHANNEL_2, new DummyMessage(202));
		Assert.assertEquals(2, messageForwarder.getMessagesReceived().size());
		Assert.assertEquals(0, messageForwarder.getMessagesSent().size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(LEFT_CHANNEL_2).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(RIGHT_CHANNEL_1).size());
		Assert.assertEquals(0, messageHandler.getMessagesReceived(RIGHT_CHANNEL_2).size());
	}
}
