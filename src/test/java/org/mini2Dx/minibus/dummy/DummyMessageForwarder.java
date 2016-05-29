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

import java.util.ArrayList;
import java.util.List;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.handler.MessageForwarder;

/**
 * A dummy {@link MessageForwarder} for unit tests
 */
public class DummyMessageForwarder extends MessageForwarder {
	private boolean forwardMessages = true;
	
	private final List<Message> messagesReceived = new ArrayList<Message>();
	private final List<Message> messagesSent = new ArrayList<Message>();

	public DummyMessageForwarder(MessageExchange... receivers) {
		super(receivers);
	}

	@Override
	public boolean forward(Message message) {
		messagesReceived.add(message);
		if(forwardMessages) {
			messagesSent.add(message);
		}
		return forwardMessages;
	}

	public List<Message> getMessagesReceived() {
		return messagesReceived;
	}
	
	public List<Message> getMessagesSent() {
		return messagesSent;
	}
	
	public void setForwardMessages(boolean forwardMessages) {
		this.forwardMessages = forwardMessages;
	}

}
