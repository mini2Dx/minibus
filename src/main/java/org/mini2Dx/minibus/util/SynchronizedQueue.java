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

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class SynchronizedQueue<T> implements Queue<T> {
	private final SnapshotArrayList<T> queue = new SnapshotArrayList<T>();

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return queue.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T1> T1[] toArray(T1[] a) {
		return queue.toArray(a);
	}

	@Override
	public boolean add(T t) {
		return queue.add(t);
	}

	@Override
	public boolean remove(Object o) {
		return queue.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return queue.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return queue.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return queue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return queue.retainAll(c);
	}

	@Override
	public void clear() {
		queue.clear();
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
		if(queue.size() == 0) {
			return null;
		}
		return queue.safeRemove(0);
	}

	@Override
	public T element() {
		if(queue.size() == 0) {
			return null;
		}
		return queue.get(0);
	}

	@Override
	public T peek() {
		return element();
	}

	public T get(int index) {
		return queue.get(index);
	}

	public T remove(int index) {
		return queue.remove(index);
	}
}
