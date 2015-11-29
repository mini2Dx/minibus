/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

/**
 *
 */
public interface MessageHandler {

	public void onMessageReceived(String channel, Message message);
}
