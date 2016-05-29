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
package org.mini2Dx.minibus.dummy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.MessageHandler;

/**
 * A dummy {@link MessageHandler} for unit tests
 */
public class DummyMessageHandler implements MessageHandler {
	private final Map<Integer, List<Message>> messagesReceived = new HashMap<Integer, List<Message>>();
	
	@Override
	public void onMessageReceived(MessageExchange source, MessageExchange receiver, Message message) {
		if(!messagesReceived.containsKey(source.getId())) {
			messagesReceived.put(source.getId(), new CopyOnWriteArrayList<Message>());
		}
		messagesReceived.get(source.getId()).add(message);
	}

	public List<Message> getMessagesReceived(int exchangeId) {
		if(!messagesReceived.containsKey(exchangeId)) {
			messagesReceived.put(exchangeId, new CopyOnWriteArrayList<Message>());
		}
		return messagesReceived.get(exchangeId);
	}
}
