/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 See AUTHORS file
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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link MessageTransmission}
 */
public class MessageTransmissionTest {
	private Mockery mockery;
	private MessageTransmissionPool transmissionPool;
	private MessageTransmission transmission;
	
	@Before
	public void setUp() {
		mockery = new Mockery();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		
		transmissionPool = mockery.mock(MessageTransmissionPool.class);
		transmission = new MessageTransmission(transmissionPool);
	}
	
	@Test
	public void testSingleAllocateRelease() {
		mockery.checking(new Expectations() {
			{
				oneOf(transmissionPool).release(transmission);
			}
		});
		
		transmission.allocate();
		transmission.release();
	}
	
	@Test
	public void testMultipleAllocateRelease() {
		mockery.checking(new Expectations() {
			{
				oneOf(transmissionPool).release(transmission);
			}
		});
		
		for(int i = 0; i < 100; i++) {
			transmission.allocate();
		}
		for(int i = 0; i < 100; i++) {
			transmission.release();
		}
	}
}
