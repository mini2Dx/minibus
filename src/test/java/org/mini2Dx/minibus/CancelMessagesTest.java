/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 See AUTHORS file
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
package org.mini2Dx.minibus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mini2Dx.minibus.dummy.DummyEntityMessageData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CancelMessagesTest implements MessageHandler, CancelledMessageHandler {
	private static final String MESSAGE_TYPE_A = "messageA";
	private static final String MESSAGE_TYPE_B = "messageB";
	private static final String MESSAGE_TYPE_C = "messageC";

	private final MessageBus messageBus = new MessageBus();
	private final MessageExchange messageExchange = messageBus.createOnUpdateExchange(this);

	private final Set<String> receivedMessages = new HashSet<>();
	private final Set<String> cancelledMessages = new HashSet<>();

	@Before
	public void setUp() {
		messageBus.addCancelledMessageHandler(this);
	}

	@After
	public void teardown() {
		messageBus.dispose(messageExchange);
	}

	@Test
	public void testCancelAllMessages() {
		messageBus.broadcast(MESSAGE_TYPE_A, new DummyEntityMessageData(1));
		messageBus.broadcast(MESSAGE_TYPE_B, new DummyEntityMessageData(2));
		messageBus.broadcast(MESSAGE_TYPE_C, new DummyEntityMessageData(3));

		messageBus.cancelAllMessages();
		Assert.assertEquals(3, cancelledMessages.size());
		Assert.assertEquals(0, receivedMessages.size());
		Assert.assertEquals(0, messageExchange.getMessageQueueSize());
	}

	@Test
	public void testCancelAllMessagesNoNotify() {
		messageBus.broadcast(MESSAGE_TYPE_A, new DummyEntityMessageData(1));
		messageBus.broadcast(MESSAGE_TYPE_B, new DummyEntityMessageData(2));
		messageBus.broadcast(MESSAGE_TYPE_C, new DummyEntityMessageData(3));

		messageBus.cancelAllMessages(false);
		Assert.assertEquals(0, cancelledMessages.size());
		Assert.assertEquals(0, receivedMessages.size());
		Assert.assertEquals(0, messageExchange.getMessageQueueSize());
	}

	@Test
	public void testCancelAllMessagesByType() {
		messageBus.broadcast(MESSAGE_TYPE_A, new DummyEntityMessageData(1));
		messageBus.broadcast(MESSAGE_TYPE_B, new DummyEntityMessageData(2));
		messageBus.broadcast(MESSAGE_TYPE_C, new DummyEntityMessageData(3));

		messageBus.cancelAllMessages(MESSAGE_TYPE_B);
		Assert.assertEquals(1, cancelledMessages.size());
		Assert.assertEquals(0, receivedMessages.size());
		Assert.assertEquals(2, messageExchange.getMessageQueueSize());
		Assert.assertTrue(cancelledMessages.contains(MESSAGE_TYPE_B));

		messageBus.update(0.16f);
		Assert.assertTrue(receivedMessages.contains(MESSAGE_TYPE_A));
		Assert.assertFalse(receivedMessages.contains(MESSAGE_TYPE_B));
		Assert.assertTrue(receivedMessages.contains(MESSAGE_TYPE_C));
	}

	@Test
	public void testCancelAllMessagesByTypeNoNotify() {
		messageBus.broadcast(MESSAGE_TYPE_A, new DummyEntityMessageData(1));
		messageBus.broadcast(MESSAGE_TYPE_B, new DummyEntityMessageData(2));
		messageBus.broadcast(MESSAGE_TYPE_C, new DummyEntityMessageData(3));

		messageBus.cancelAllMessages(MESSAGE_TYPE_B, false);
		Assert.assertEquals(0, cancelledMessages.size());
		Assert.assertEquals(0, receivedMessages.size());
		Assert.assertEquals(2, messageExchange.getMessageQueueSize());

		messageBus.update(0.16f);
		Assert.assertTrue(receivedMessages.contains(MESSAGE_TYPE_A));
		Assert.assertFalse(receivedMessages.contains(MESSAGE_TYPE_B));
		Assert.assertTrue(receivedMessages.contains(MESSAGE_TYPE_C));
	}

	@Override
	public void onMessageCancelled(String messageType, MessageExchange source, MessageExchange receiver, MessageData messageData) {
		cancelledMessages.add(messageType);
	}

	@Override
	public void onMessageReceived(String messageType, MessageExchange source, MessageExchange receiver, MessageData messageData) {
		receivedMessages.add(messageType);
	}
}
