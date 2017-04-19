/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 See AUTHORS file
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
package org.mini2Dx.minibus.pool;

import org.mini2Dx.minibus.MessageData;

/**
 * Base class for {@link MessageData} implementations that can be optionally
 * pooled and managed by a {@link MessageDataPool} instance
 */
public class OptionallyPooledMessageData implements PooledMessageData {
	private final MessageDataPool<PooledMessageData> pool;

	/**
	 * Non-pooled constructor
	 */
	public OptionallyPooledMessageData() {
		this(null);
	}

	/**
	 * Pooled constructor
	 * 
	 * @param pool
	 *            The {@link MessageDataPool} managing this instance
	 */
	public OptionallyPooledMessageData(MessageDataPool<PooledMessageData> pool) {
		super();
		this.pool = pool;
	}

	@Override
	public void release() {
		if (pool == null) {
			return;
		}
		pool.release(this);
	}

}
