/**
 * Copyright (c) 2016 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of mini2Dx, minibus nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.minibus.message;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.TransactionState;
import org.mini2Dx.minibus.util.TransactionIdGenerator;

/**
 * A base class for {@link Message}s that are part of a transaction. The
 * transaction is identified by an integer id.
 * 
 * A transaction ids are generated using {@link TransactionIdGenerator}.
 */
public abstract class TransactionMessage implements Message {
	private final TransactionState transactionState;
	private final int transactionId;

	/**
	 * Constructs a {@link TransactionState#NOTIFY} {@link TransactionMessage}
	 * with a new transaction id
	 */
	public TransactionMessage() {
		this(TransactionIdGenerator.getNextId());
	}

	/**
	 * Constructs a {@link TransactionState#NOTIFY} {@link TransactionMessage}
	 * with a specific transaction id
	 * 
	 * @param transactionId
	 */
	public TransactionMessage(int transactionId) {
		this(transactionId, TransactionState.NOTIFY);
	}

	/**
	 * Constructs a {@link TransactionMessage} with a specific
	 * {@link TransactionState} and a new transaction id
	 * 
	 * @param transactionState
	 */
	public TransactionMessage(TransactionState transactionState) {
		this(TransactionIdGenerator.getNextId(), transactionState);
	}

	/**
	 * Constructs a {@link TransactionMessage} with an existing transaction id
	 * and {@link TransactionState}
	 * 
	 * @param transactionId
	 *            The transaction id
	 * @param transactionState
	 *            The {@link TransactionState}
	 * 
	 */
	public TransactionMessage(int transactionId, TransactionState transactionState) {
		this.transactionId = transactionId;
		this.transactionState = transactionState;
	}

	@Override
	public int getTransactionId() {
		return transactionId;
	}

	@Override
	public TransactionState getTransactionState() {
		return transactionState;
	}
}
