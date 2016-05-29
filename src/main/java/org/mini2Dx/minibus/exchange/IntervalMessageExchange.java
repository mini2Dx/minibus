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
package org.mini2Dx.minibus.exchange;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageExchange;
import org.mini2Dx.minibus.MessageHandler;

/**
 * Processes {@link Message}s at a regular interval
 */
public class IntervalMessageExchange extends MessageExchange {
	private final float interval;

	private float timer;
	
	/**
	 * Constructor
	 * @param interval The interval between processing {@link Message}s (in seconds)
	 * @param messageBus The {@link MessageBus} that created this {@link IntervalMessageExchange}
	 * @param messageHandler The {@link MessageHandler} to send messages to
	 */
	public IntervalMessageExchange(float interval, MessageBus messageBus, MessageHandler messageHandler) {
		super(messageBus, messageHandler);
		this.interval = interval;
	}

	@Override
	public void update(float delta) {
		timer += delta;
		if(timer >= interval) {
			timer -= interval;
			flush();
		}
	}

	@Override
	public boolean isImmediate() {
		return false;
	}
}
