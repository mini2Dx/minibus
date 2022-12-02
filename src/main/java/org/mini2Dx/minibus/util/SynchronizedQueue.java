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

import org.mini2Dx.lockprovider.ReadWriteLock;
import org.mini2Dx.minibus.MessageBus;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class SynchronizedQueue<T> implements Queue<T> {
	private final ReadWriteLock lock = MessageBus.LOCK_PROVIDER.newReadWriteLock();

	private Object[] values = new Object[16];
	private int head = 0;
	private int tail = 0;
	private int size = 0;

	@Override
	public int size() {
		lock.lockRead();
		int result = size;
		lock.unlockRead();
		return result;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		boolean result = false;
		lock.lockRead();
		for(int i = 0; i < size; i++) {
			if(values[i].equals(o)) {
				result = true;
				break;
			}
		}
		lock.unlockRead();
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
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
		lock.lockWrite();

		Object[] values = this.values;

		if (size == values.length) {
			resize(values.length << 1);
			values = this.values;
		}

		values[tail++] = t;
		if (tail == values.length) {
			tail = 0;
		}
		size++;

		lock.unlockWrite();
		return true;
	}

	@Override
	public boolean remove(Object o) {
		lock.lockRead();
		int index = indexOf(o, true);
		lock.unlockRead();
		if (index == -1) {
			return false;
		}
		remove(index);
		return true;
	}

	public int indexOf (Object value, boolean identity) {
		if (size == 0) {
			return -1;
		}
		Object[] values = this.values;
		final int head = this.head, tail = this.tail;
		if (identity || value == null) {
			if (head < tail) {
				for (int i = head; i < tail; i++) {
					if (values[i] == value)  {
						return i - head;
					}
				}
			} else {
				for (int i = head, n = values.length; i < n; i++) {
					if (values[i] == value) {
						return i - head;
					}
				}
				for (int i = 0; i < tail; i++) {
					if (values[i] == value) {
						return i + values.length - head;
					}
				}
			}
		} else {
			if (head < tail) {
				for (int i = head; i < tail; i++) {
					if (value.equals(values[i]))  {
						return i - head;
					}
				}
			} else {
				for (int i = head, n = values.length; i < n; i++) {
					if (value.equals(values[i])) {
						return i - head;
					}
				}
				for (int i = 0; i < tail; i++) {
					if (value.equals(values[i])) {
						return i + values.length - head;
					}
				}
			}
		}
		return -1;
	}

	public T get(int index) {
		lock.lockRead();
		final Object[] values = this.values;

		int i = head + index;
		if (i >= values.length) {
			i -= values.length;
		}
		final Object result = values[i];
		lock.unlockRead();
		return (T) result;
	}

	public T remove(int index) {
		return index == 0 ? removeFirst() : removeAt(index);
	}

	private T removeFirst() {
		lock.lockWrite();

		if (size == 0) {
			lock.unlockWrite();
			return null;
		}

		final Object[] values = this.values;

		final Object result = values[head];
		values[head] = null;
		head++;
		if (head == values.length) {
			head = 0;
		}
		size--;

		lock.unlockWrite();
		return (T) result;
	}

	private T removeAt(int index) {
		lock.lockWrite();
		if (index < 0) {
			lock.unlockWrite();
			return null;
		}
		if (index >= size) {
			lock.unlockWrite();
			return null;
		}

		Object[] values = this.values;
		int head = this.head, tail = this.tail;
		index += head;
		Object value;
		if (head < tail) { // index is between head and tail.
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			values[tail] = null;
			this.tail--;
		} else if (index >= values.length) { // index is between 0 and tail.
			index -= values.length;
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			this.tail--;
		} else { // index is between head and values.length.
			value = values[index];
			System.arraycopy(values, head, values, head + 1, index - head);
			values[head] = null;
			this.head++;
			if (this.head == values.length) {
				this.head = 0;
			}
		}
		size--;
		lock.unlockWrite();
		return (T) value;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = false;
		for(Object item : c) {
			result |= contains(item);
		}
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		for(T item : c) {
			result |= add(item);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for(Object item : c) {
			result |= remove(item);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		lock.lockWrite();

		if (size == 0) {
			lock.unlockWrite();
			return;
		}
		final Object[] values = this.values;
		final int head = this.head;
		final int tail = this.tail;

		if (head < tail) {
			// Continuous
			for (int i = head; i < tail; i++) {
				values[i] = null;
			}
		} else {
			// Wrapped
			for (int i = head; i < values.length; i++) {
				values[i] = null;
			}
			for (int i = 0; i < tail; i++) {
				values[i] = null;
			}
		}
		this.head = 0;
		this.tail = 0;
		this.size = 0;

		lock.unlockWrite();
	}

	@Override
	public boolean offer(T t) {
		return add(t);
	}

	@Override
	public T remove() {
		return poll();
	}

	@Override
	public T poll() {
		return remove(0);
	}

	@Override
	public T element() {
		return get(0);
	}

	@Override
	public T peek() {
		return get(0);
	}

	private void resize (int capacity) {
		final Object[] values = this.values;
		final int head = this.head;
		final int tail = this.tail;

		final Object [] newArray = new Object[Math.max(values.length * 2, capacity + 1)];

		if (head < tail) {
			// Continuous
			System.arraycopy(values, head, newArray, 0, tail - head);
		} else if (size > 0) {
			// Wrapped
			final int rest = values.length - head;
			System.arraycopy(values, head, newArray, 0, rest);
			System.arraycopy(values, 0, newArray, rest, tail);
		}
		this.values = newArray;
		this.head = 0;
		this.tail = size;
	}
}
