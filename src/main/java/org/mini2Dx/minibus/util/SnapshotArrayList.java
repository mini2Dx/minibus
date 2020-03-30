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
package org.mini2Dx.minibus.util;

import org.mini2Dx.minibus.MessageBus;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;

public class SnapshotArrayList<T> implements List<T> {
	private final Object iteratorLock = new Object();
	private final ReadWriteLock lock = MessageBus.LOCK_PROVIDER.newReadWriteLock();
	private final Queue<SnapshotIterator<T>> iteratorPool = new ArrayDeque<SnapshotIterator<T>>();
	private Object[] array = new Object[32];

	private int size = 0;

	private void ensureCapacity(int capacity) {
		ArrayList list = new ArrayList();
		if(capacity <= array.length) {
			return;
		}
		Object [] newArray = new Object[Math.max(array.length * 2, capacity + 1)];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}

	@Override
	public int size() {
		final int result;
		lock.readLock().lock();
		result = size;
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
		for(int i = 0; i < size; i++) {
			if(array[i].equals(o)) {
				result = true;
				break;
			}
		}
		lock.readLock().unlock();
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		final SnapshotIterator<T> result;
		synchronized (iteratorLock) {
			if(iteratorPool.isEmpty()) {
				result = new SnapshotIterator<T>(iteratorLock, iteratorPool);
			} else {
				result = iteratorPool.poll();
			}
		}
		lock.readLock().lock();
		result.init(array, size);
		lock.readLock().unlock();
		return result;
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(T t) {
		lock.writeLock().lock();
		ensureCapacity(size + 1);
		array[size] = t;
		size++;
		lock.writeLock().unlock();
		return true;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = false;

		lock.writeLock().lock();
		for(int i = 0; i < size; i++) {
			if(!array[i].equals(o)) {
				continue;
			}
			array[i] = array[size - 1];
			array[size - 1] = null;
			size--;
			result = true;
			break;
		}
		lock.writeLock().unlock();

		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c) {
			if(!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		lock.writeLock().lock();
		ensureCapacity(size + c.size());
		lock.writeLock().unlock();
		for(T o : c) {
			add(o);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for(Object o : c) {
			result |= remove(o);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		for(int i = 0; i < array.length; i++) {
			array[i] = null;
		}
		size = 0;
		lock.writeLock().unlock();
	}

	@Override
	public T get(int index) {
		T result = null;
		lock.readLock().lock();
		if(index < 0) {
			lock.readLock().unlock();
			throw new IndexOutOfBoundsException();
		}
		if(index >= size) {
			lock.readLock().unlock();
			throw new IndexOutOfBoundsException();
		}
		result = (T) array[index];
		lock.readLock().unlock();
		return result;
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		Object result = null;
		lock.writeLock().lock();
		if(index < 0) {
			lock.writeLock().unlock();
			throw new IndexOutOfBoundsException();
		}
		if(index >= size) {
			lock.writeLock().unlock();
			throw new IndexOutOfBoundsException();
		}

		result = array[index];
		array[index] = array[size - 1];
		size--;
		lock.writeLock().unlock();
		return (T) result;
	}

	@Override
	public int indexOf(Object o) {
		lock.readLock().lock();
		for(int i = 0; i < size; i++) {
			if(array[i].equals(o)) {
				lock.readLock().unlock();
				return i;
			}
		}
		lock.readLock().unlock();
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		lock.readLock().lock();
		for(int i = size - 1; i >= 0; i--) {
			if(array[i].equals(o)) {
				lock.readLock().unlock();
				return i;
			}
		}
		lock.readLock().unlock();
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new IndexOutOfBoundsException();
	}

	private class SnapshotIterator<T> implements Iterator<T> {
		private final Object lock;
		private final Queue<SnapshotIterator<T>> iteratorPool;

		private Object [] array;
		private int size;
		private int index;

		public SnapshotIterator(Object lock, Queue<SnapshotIterator<T>> iteratorPool) {
			this.lock = lock;
			this.iteratorPool = iteratorPool;
		}

		public void init(Object [] source, int size) {
			this.size = size;
			this.index = 0;

			if(array == null || source.length > array.length) {
				array = new Object[source.length];
			}
			System.arraycopy(source, 0, array, 0, source.length);
		}

		@Override
		public boolean hasNext() {
			if(index == size) {
				synchronized (lock) {
					iteratorPool.add(this);
				}
				return false;
			}
			return true;
		}

		@Override
		public T next() {
			final T result = (T) array[index];
			index++;
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
