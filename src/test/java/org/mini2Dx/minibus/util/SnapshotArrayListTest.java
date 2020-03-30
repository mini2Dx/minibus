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

import org.junit.Assert;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

public class SnapshotArrayListTest {

	@Test
	public void testSingleThreadAdd() {
		final SnapshotArrayList<Object> list = new SnapshotArrayList<Object>();
		list.add(new Object());
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void testMultiThreadAdd() {
		final CountDownLatch latch = new CountDownLatch(2);
		final SnapshotArrayList<Object> list = new SnapshotArrayList<Object>();

		final Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				for(int i = 0; i < 100; i++) {
					list.add(new Object());
				}
			}
		});
		final Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				for(int i = 0; i < 100; i++) {
					list.add(new Object());
				}
			}
		});

		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (Exception e) {}

		Assert.assertEquals(200, list.size());
	}

	@Test
	public void testSingleThreadIterator() {
		final SnapshotArrayList<Object> list = new SnapshotArrayList<Object>();
		for(int i = 0; i < 100; i++) {
			list.add(new Object());
		}

		int result = 0;
		for(Object o : list) {
			result++;
		}

		Assert.assertEquals(100, result);
	}

	@Test
	public void testMultiThreadAddRemove() {
		final CountDownLatch latch = new CountDownLatch(2);
		final SnapshotArrayList<Object> list = new SnapshotArrayList<Object>();
		final Queue<Object> removeQueue = new ConcurrentLinkedQueue<>();

		final Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				for(int i = 0; i < 100; i++) {
					final Object obj = new Object();
					list.add(obj);
					removeQueue.offer(obj);
				}
			}
		});
		final Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				for(int i = 0; i < 100; i++) {
					while(!removeQueue.isEmpty()) {
						final Object obj = removeQueue.peek();
						if(list.remove(obj)) {
							removeQueue.poll();
						}
					}
				}
			}
		});

		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (Exception e) {}

		Assert.assertEquals(0, list.size());
	}
}
