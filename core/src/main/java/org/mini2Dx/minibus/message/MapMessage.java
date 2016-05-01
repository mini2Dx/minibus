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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mini2Dx.minibus.Message;
import org.mini2Dx.minibus.TransactionState;

/**
 * A {@link Message} that also implements the {@link Map} interface, backed by a
 * {@link Map} instance.
 */
public class MapMessage<K, V> extends TransactionMessage implements Map<K, V> {
	private final Map<K, V> hashMap;
	private final String messageType;

	/**
	 * Constructs a new {@link TransactionState#NOTIFY} {@link MapMessage}
	 * backed by a {@link HashMap}
	 * 
	 * @param messageType
	 *            The message type
	 */
	public MapMessage(String messageType) {
		this(messageType, new HashMap<K, V>());
	}

	/**
	 * Constructs a new {@link MapMessage} with a new transaction id and backed
	 * by {@link HashMap}
	 * 
	 * @param messageType
	 *            The message type
	 * @param transactionState
	 *            The {@link TransactionState}
	 */
	public MapMessage(String messageType, TransactionState transactionState) {
		this(messageType, transactionState, new HashMap<K, V>());
	}

	/**
	 * Constructs a new {@link MapMessage} backed by {@link HashMap}
	 * 
	 * @param messageType
	 *            The message type
	 * @param transactionId
	 *            The transaction id
	 * @param transactionState
	 *            The {@link TransactionState}
	 */
	public MapMessage(String messageType, int transactionId, TransactionState transactionState) {
		this(messageType, transactionId, transactionState, new HashMap<K, V>());
	}

	/**
	 * Constructs a new {@link TransactionState#NOTIFY} {@link MapMessage}
	 * 
	 * @param messageType
	 *            The message type
	 * @param hashMap
	 *            The backing {@link Map} instance
	 */
	public MapMessage(String messageType, Map<K, V> hashMap) {
		super();
		this.messageType = messageType;
		this.hashMap = hashMap;
	}

	/**
	 * Constructs a new {@link MapMessage}
	 * 
	 * @param messageType
	 *            The message type
	 * @param transactionState
	 *            The {@link TransactionState}
	 * @param hashMap
	 *            The backing {@link Map} instance
	 */
	public MapMessage(String messageType, TransactionState transactionState, Map<K, V> hashMap) {
		super(transactionState);
		this.messageType = messageType;
		this.hashMap = hashMap;
	}

	/**
	 * Constructs a new {@link MapMessage}
	 * 
	 * @param messageType
	 *            The message type
	 * @param transactionId
	 *            The transaction id
	 * @param transactionState
	 *            The {@link TransactionState}
	 * @param hashMap
	 *            The backing {@link Map} instance
	 */
	public MapMessage(String messageType, int transactionId, TransactionState transactionState, Map<K, V> hashMap) {
		super(transactionId, transactionState);
		this.messageType = messageType;
		this.hashMap = hashMap;
	}

	@Override
	public String getMessageType() {
		return messageType;
	}

	@Override
	public int size() {
		return hashMap.size();
	}

	@Override
	public boolean isEmpty() {
		return hashMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return hashMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return hashMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return hashMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		return hashMap.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return hashMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		hashMap.putAll(m);
	}

	@Override
	public void clear() {
		hashMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return hashMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return hashMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return hashMap.entrySet();
	}
}
