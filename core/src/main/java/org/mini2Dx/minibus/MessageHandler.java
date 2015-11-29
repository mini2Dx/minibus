/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

/**
 * Common interface for {@link Message} processing implementations
 */
public interface MessageHandler {

	/**
	 * Called when a {@link Message} is received
	 * @param channel The channel the {@link Message} was received on
	 * @param message The {@link Message} that was received
	 */
	public void onMessageReceived(String channel, Message message);
}
