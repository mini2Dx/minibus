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

import java.util.HashSet;
import java.util.Set;

public class DeleteEntityTest implements MessageHandler, CancelledMessageHandler {
	private static final String MESSAGE_TYPE = "messageA";

	private final MessageBus messageBus = new MessageBus();
	private final MessageExchange messageExchange = messageBus.createOnUpdateExchange(this);

	private final Set<Integer> receivedEntities = new HashSet<>();
	private final Set<Integer> cancelledEntities = new HashSet<>();

	@Before
	public void setUp() {
		messageBus.addCancelledMessageHandler(this);
	}

	@After
	public void teardown() {
		messageBus.dispose(messageExchange);
	}

	@Test
	public void testDeleteEntity() {
		messageBus.broadcast(MESSAGE_TYPE, new DummyEntityMessageData(1));
		messageBus.broadcast(MESSAGE_TYPE, new DummyEntityMessageData(2));
		messageBus.broadcast(MESSAGE_TYPE, new DummyEntityMessageData(3));

		messageBus.entityDeleted(2);
		Assert.assertFalse(cancelledEntities.contains(1));
		Assert.assertTrue(cancelledEntities.contains(2));
		Assert.assertFalse(cancelledEntities.contains(3));

		messageBus.update(0.16f);
		Assert.assertTrue(receivedEntities.contains(1));
		Assert.assertFalse(receivedEntities.contains(2));
		Assert.assertTrue(receivedEntities.contains(3));
	}

	@Override
	public void onMessageCancelled(String messageType, MessageExchange source, MessageExchange receiver, MessageData messageData) {
		if(messageData instanceof EntityMessageData) {
			cancelledEntities.add(((EntityMessageData) messageData).getEntityId());
		}
	}

	@Override
	public void onMessageReceived(String messageType, MessageExchange source, MessageExchange receiver, MessageData messageData) {
		if(messageData instanceof EntityMessageData) {
			receivedEntities.add(((EntityMessageData) messageData).getEntityId());
		}
	}
}
