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

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.exception.MissingPooledConstructorException;
import org.mini2Dx.minibus.messagedata.ListMessageData;
import org.mini2Dx.minibus.transmission.MessageTransmission;
import org.mini2Dx.minibus.transmission.SynchronizedQueue;

/**
 * Implements pooling for {@link PooledMessageData} instances. To use this
 * class, the {@link PooledMessageData} implementation must have a constructor
 * with a single parameter of type {@link MessageDataPool} (see
 * {@link ListMessageData} for an example)
 */
public class MessageDataPool<T extends PooledMessageData> {
	public static final int DEFAULT_POOL_SIZE = 5;

	private final Queue<T> pool;
	private final Constructor<T> constructor;

	/**
	 * Constructs a new {@link MessageDataPool} of size {@link #DEFAULT_POOL_SIZE}<br>
	 * <br>
	 * Note: This constructor is useful when T also has a generic type, e.g. {@link ListMessageData}
	 * 
	 * @param instance The instance to derive T from
	 */
	public MessageDataPool(T instance) {
		this((Class<T>) instance.getClass());
	}

	/**
	 * Constructs a new {@link MessageDataPool} of size {@link #DEFAULT_POOL_SIZE}
	 * @param clazz The class of type T
	 */
	public MessageDataPool(Class<T> clazz) {
		super();
		try {
			constructor = clazz.getConstructor(MessageDataPool.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new MissingPooledConstructorException(clazz);
		}

		if(MessageBus.USE_JAVA_UTIL_CONCURRENT) {
			pool = new ConcurrentLinkedQueue<T>();
		} else {
			pool = new SynchronizedQueue<T>();
		}

		for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
			pool.offer(createNewInstance());
		}
	}

	/**
	 * Allocates an instance from the pool
	 * @return An instance of T
	 */
	public T allocate() {
		if (pool.isEmpty()) {
			return createNewInstance();
		}
		return pool.poll();
	}

	/**
	 * Returns an instance back to the pool
	 * @param instance An instance of T
	 */
	public void release(T instance) {
		pool.offer(instance);
	}

	private T createNewInstance() {
		try {
			return constructor.newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the number of instances of T available in the pool
	 * @return 0 if empty (new instances will be created but may slow performance)
	 */
	public int getCurrentPoolSize() {
		return pool.size();
	}
}
