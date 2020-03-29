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
package org.mini2Dx.minibus.transmission;

import java.util.*;

public class SynchronizedQueue<T> implements Queue<T> {
	private final Queue<T> queue = new ArrayDeque<T>(32);

	@Override
	public int size() {
		int result = 0;
		synchronized(queue) {
			result = queue.size();
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		boolean result = false;
		synchronized(queue) {
			result = queue.contains(o);
		}
		return result;
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> result = null;
		synchronized(queue) {
			result = queue.iterator();
		}
		return result;
	}

	@Override
	public Object[] toArray() {
		Object [] result = null;
		synchronized(queue) {
			result = queue.toArray();
		}
		return result;
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		T1[] result = null;
		synchronized(queue) {
			result = queue.toArray(a);
		}
		return result;
	}

	@Override
	public boolean add(T t) {
		boolean result = false;
		synchronized(queue) {
			result = queue.add(t);
		}
		return result;
	}

	@Override
	public boolean remove(Object o) {
		boolean result = false;
		synchronized(queue) {
			result = queue.remove(o);
		}
		return result;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		boolean result = false;
		synchronized(queue) {
			result = queue.containsAll(c);
		}
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean result = false;
		synchronized(queue) {
			result = queue.addAll(c);
		}
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		synchronized(queue) {
			result = queue.removeAll(c);
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean result = false;
		synchronized(queue) {
			result = queue.retainAll(c);
		}
		return result;
	}

	@Override
	public void clear() {
		synchronized(queue) {
			queue.clear();
		}
	}

	@Override
	public boolean offer(T t) {
		return add(t);
	}

	@Override
	public T remove() {
		T result = null;
		synchronized(queue) {
			result = queue.remove();
		}
		return result;
	}

	@Override
	public T poll() {
		T result = null;
		synchronized(queue) {
			result = queue.poll();
		}
		return result;
	}

	@Override
	public T element() {
		T result = null;
		synchronized(queue) {
			result = queue.element();
		}
		return result;
	}

	@Override
	public T peek() {
		T result = null;
		synchronized(queue) {
			result = queue.peek();
		}
		return result;
	}
}
