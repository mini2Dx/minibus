/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus;

/**
 * Common interface to message consumer implementations
 */
public interface MessageConsumer extends MessageHandler {
	/**
	 * Updates this {@link MessageConsumer}
	 * 
	 * @param delta
	 *            (in seconds) The timestep or amount of time that has elapsed
	 *            since the last frame
	 */
	public void update(float delta);

	/**
	 * Subscribes this consumer to a channel on the {@link MessageBus}
	 * 
	 * @param channel
	 *            The channel to subscribe to (the channel will be created if it
	 *            doesn't exist)
	 */
	public void subscribe(String channel);

	/**
	 * Unsubscribes this consumer from a channel on the {@link MessageBus}
	 * 
	 * @param channel
	 *            The channel to unsubscribe from
	 */
	public void unsubscribe(String channel);

	/**
	 * Returns if this consumer immediately processes {@link Message}s
	 * 
	 * @return True if this {@link MessageConsumer} should process
	 *         {@link Message}s immediately as they are published
	 */
	public boolean isImmediate();

	/**
	 * Disposes of this {@link MessageConsumer} so that it can no longer be used
	 */
	public void dispose();
}
