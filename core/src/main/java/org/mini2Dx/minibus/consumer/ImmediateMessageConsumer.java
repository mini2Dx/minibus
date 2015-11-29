/**
 * Copyright 2015 Thomas Cashman
 */
package org.mini2Dx.minibus.consumer;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.MessageBus;
import org.mini2Dx.minibus.MessageConsumer;
import org.mini2Dx.minibus.MessageHandler;

/**
 * Consumes {@link Message}s immediately - effectively implements the
 * <a href="https://en.wikipedia.org/wiki/Observer_pattern">Observer pattern</a>
 * through the {@link MessageBus}
 */
public class ImmediateMessageConsumer extends BaseMessageConsumer implements MessageConsumer {
	
	public ImmediateMessageConsumer(MessageBus messageBus, MessageHandler messageHandler) {
		super(messageBus, messageHandler);
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public boolean isImmediate() {
		return true;
	}
}
