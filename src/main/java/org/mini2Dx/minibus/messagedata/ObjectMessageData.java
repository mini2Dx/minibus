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
package org.mini2Dx.minibus.messagedata;

import org.mini2Dx.minibus.MessageData;
import org.mini2Dx.minibus.pool.MessageDataPool;
import org.mini2Dx.minibus.pool.OptionallyPooledMessageData;
import org.mini2Dx.minibus.pool.PooledMessageData;

/**
 * A {@link MessageData} that stores an object
 */
public class ObjectMessageData<T> extends OptionallyPooledMessageData {
	private T obj;
	
	/**
	 * Constructs a non-pooled {@link ObjectMessageData} instance
	 * @param obj The object instance
	 */
	public ObjectMessageData(T obj) {
		super();
		this.obj = obj;
	}
	
	/**
	 * Constructs a pooled {@link ObjectMessageData} instance
	 * @param pool The {@link MessageDataPool} managing this instance
	 */
	public ObjectMessageData(MessageDataPool<PooledMessageData> pool) {
		super(pool);
	}
	
	public T getObject() {
		return obj;
	}

	public void setObject(T obj) {
		this.obj = obj;
	}
}
