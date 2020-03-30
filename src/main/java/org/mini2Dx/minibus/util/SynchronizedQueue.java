/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 See AUTHORS file
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
package org.mini2Dx.minibus.util;

import org.mini2Dx.minibus.MessageBus;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public class SynchronizedQueue<T> implements Queue<T> {
	private ReadWriteLock lock = MessageBus.LOCK_PROVIDER.newReadWriteLock();
	private final Queue<T> queue = new ArrayDeque<T>();

	@Override
	public int size() {
		int result = 0;
		lock.readLock().lock();
		result = queue.size();
		lock.readLock().unlock();
		return result;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		boolean result = false;
		lock.readLock().lock();
		result = queue.contains(o);
		lock.readLock().unlock();
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		Object [] result = null;
		lock.readLock().lock();
		result = queue.toArray();
		lock.readLock().unlock();
		return result;
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		T1[] result = null;
		lock.readLock().lock();
		result = queue.toArray(a);
		lock.readLock().unlock();
		return result;
	}

	@Override
	public boolean add(T t) {
		boolean result = false;
		lock.writeLock().lock();
		result = queue.add(t);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = false;
		lock.writeLock().lock();
		result = queue.remove(o);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = false;
		lock.readLock().lock();
		result = queue.containsAll(c);
		lock.readLock().unlock();
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		lock.writeLock().lock();
		result = queue.addAll(c);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		lock.writeLock().lock();
		result = queue.removeAll(c);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		lock.writeLock().lock();
		result = queue.retainAll(c);
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		queue.clear();
		lock.writeLock().unlock();
	}

	@Override
	public boolean offer(T t) {
		return add(t);
	}

	@Override
	public T remove() {
		T result = null;
		lock.writeLock().lock();
		result = queue.remove();
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public T poll() {
		T result = null;
		lock.writeLock().lock();
		result = queue.poll();
		lock.writeLock().unlock();
		return result;
	}

	@Override
	public T element() {
		T result = null;
		lock.readLock().lock();
		result = queue.element();
		lock.readLock().unlock();
		return result;
	}

	@Override
	public T peek() {
		return element();
	}
}
