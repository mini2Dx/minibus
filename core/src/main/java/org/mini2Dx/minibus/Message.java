/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

/**
 * Common interface for messages passed through the {@link MessageBus}
 */
public interface Message {

	public String getMessageType();
}
