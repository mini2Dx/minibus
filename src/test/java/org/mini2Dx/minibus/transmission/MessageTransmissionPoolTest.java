/**
 * Copyright 2021 Viridian Software Ltd.
 */
package org.mini2Dx.minibus.transmission;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class MessageTransmissionPoolTest {
	private final MessageTransmissionPool transmissionPool = new MessageTransmissionPool();

	@Test(timeout = 5000)
	public void testConcurrentAllocateRelease() {
		final int totalThreads = 4;
		final int totalAllocationsPerThread = 10000;

		final Thread [] threads = new Thread[totalThreads];
		final CountDownLatch countDownLatch = new CountDownLatch(totalThreads);
		final ArrayBlockingQueue<MessageTransmission> results = new ArrayBlockingQueue<MessageTransmission>(totalAllocationsPerThread * totalThreads);

		for(int i = 0; i < totalThreads; i++) {
			final int index = i;
			threads[i] = new Thread(() -> {
				try {
					countDownLatch.countDown();
					countDownLatch.await();
				} catch (Exception e) {}

				for(int j = 0; j < totalAllocationsPerThread; j++) {
					if(index % 2 == 0) {
						results.offer(transmissionPool.allocate());
					} else {
						try {
							final MessageTransmission transmission = results.take();
							transmission.release();
						} catch (Exception e) {}
					}
				}
			});
		}
		for(int i = 0; i < totalThreads; i++) {
			threads[i].start();
		}
		for(int i = 0; i < totalThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {}
		}
		Assert.assertEquals(0, results.size());
	}
}
