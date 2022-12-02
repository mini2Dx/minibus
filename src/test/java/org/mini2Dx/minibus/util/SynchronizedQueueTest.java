/**
 * Copyright 2022 Viridian Software Ltd.
 */
package org.mini2Dx.minibus.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class SynchronizedQueueTest {

	@Test
	public void testQueue() {
		final int count = 100;
		final SynchronizedQueue<String> queue = new SynchronizedQueue<>();
		Assert.assertTrue(queue.isEmpty());

		for(int i = 0; i < count; i++) {
			Assert.assertEquals(i, queue.size());
			queue.add("str" + i);
		}
		Assert.assertEquals(count, queue.size());
		Assert.assertFalse(queue.isEmpty());

		for(int i = 0; i < count; i++) {
			Assert.assertEquals("str" + i, queue.poll());
		}
	}

	@Test
	public void testMultiThreadQueue() {
		final int count = 1024 * 32;
		final CountDownLatch latch = new CountDownLatch(4);
		final SynchronizedQueue<Object> queue = new SynchronizedQueue<Object>();

		final Thread thread1 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				try {
					latch.await();
				} catch (Exception e) {}

				for(int i = 0; i < count; i++) {
					queue.add(new Object());
				}
			}
		});
		final Thread thread2 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				try {
					latch.await();
				} catch (Exception e) {}

				for(int i = 0; i < count; i++) {
					queue.add(new Object());
				}
			}
		});
		final Thread thread3 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				try {
					latch.await();
				} catch (Exception e) {}

				while(queue.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (Exception e) {}
				}

				while(queue.size() > 0) {
					try {
						queue.poll();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		final Thread thread4 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				try {
					latch.await();
				} catch (Exception e) {}

				while(queue.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (Exception e) {}
				}

				while(queue.size() > 0) {
					try {
						queue.peek();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		final Thread thread5 = new Thread(new Runnable() {
			@Override
			public void run() {
				latch.countDown();

				try {
					latch.await();
				} catch (Exception e) {}

				while(queue.isEmpty()) {
					try {
						Thread.sleep(1);
					} catch (Exception e) {}
				}

				while(queue.size() > 0) {
					try {
						queue.remove(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		thread5.start();

		try {
			thread1.join();
			thread2.join();
			thread3.join();
			thread4.join();
			thread5.join();
		} catch (Exception e) {}

		Assert.assertEquals(0, queue.size());
		Assert.assertTrue(queue.isEmpty());
	}
}
