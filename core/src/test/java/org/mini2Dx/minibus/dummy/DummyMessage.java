/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.dummy;

import org.mini2Dx.minibus.Message;

/**
 * A dummy {@link Message} for unit tests
 */
public class DummyMessage implements Message {
	public static final String MESSAGE_TYPE = "DUMMY";
	
	private final int value;

	public DummyMessage(int value) {
		this.value = value;
	}
	
	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DummyMessage other = (DummyMessage) obj;
		if (value != other.value)
			return false;
		return true;
	}

}
