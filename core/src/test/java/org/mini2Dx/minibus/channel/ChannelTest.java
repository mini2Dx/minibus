/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.channel;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.mini2Dx.minibus.MessageConsumer;

/**
 * Unit tests for {@link Channel}
 */
public class ChannelTest {
	private static final String CHANNEL_NAME = "test-channel";
	private static final int POOL_SIZE = 5;
	
	private final Mockery mockery = new Mockery();
	private final Channel channel = new Channel(CHANNEL_NAME, POOL_SIZE);
	
	private MessageConsumer consumer;
	
	@Before
	public void setUp() {
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		consumer = mockery.mock(MessageConsumer.class);
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoSubscriptionsAvailable() {
		for(int i = 0; i < POOL_SIZE + 1; i++) {
			channel.allocate(consumer);
		}
	}
}
