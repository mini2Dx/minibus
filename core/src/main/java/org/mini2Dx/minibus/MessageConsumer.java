/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

/**
 *
 */
public interface MessageConsumer extends MessageHandler {
	
	public void update(float delta);
	
	public void subscribe(String channel);
	
	public void unsubscribe(String channel);
	
	public boolean isImmediate();
	
	public void dispose();
}
