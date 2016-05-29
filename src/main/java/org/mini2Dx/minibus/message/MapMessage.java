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
package org.mini2Dx.minibus.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mini2Dx.minibus.Message;

/**
 * A {@link Message} that also implements the {@link Map} interface, backed by a
 * {@link Map} instance.
 */
public class MapMessage<K, V> implements Map<K, V>, Message {
	private final Map<K, V> hashMap;
	private final String messageType;

	/**
	 * Constructs a new {@link MapMessage} backed by a {@link HashMap}
	 * 
	 * @param messageType
	 *            The message type
	 */
	public MapMessage(String messageType) {
		this(messageType, new HashMap<K, V>());
	}

	/**
	 * Constructs a new {@link MapMessage}
	 * 
	 * @param messageType
	 *            The message type
	 * @param hashMap
	 *            The backing {@link Map} instance
	 */
	public MapMessage(String messageType, Map<K, V> hashMap) {
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
