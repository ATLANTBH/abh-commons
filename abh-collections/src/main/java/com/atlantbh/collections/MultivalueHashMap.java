package com.atlantbh.collections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for associating multiple values with same key.
 * 
 * @author Emir Arnautovic
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of values maintained by value list
 */
public class MultivalueHashMap<K, V> extends HashMap<K, List<V>> {

	private static final long serialVersionUID = -1526452997549605885L;
	
	/**
	 * If an entry with <code>key</code> already exists, adds nonexisting values from provided <code>values</code> to the associated list.<br />
	 * Otherwise, creates a new key-values pair.
	 * @param key
	 * @param values
	 * @return resulting list associated with key
	 */
	public List<V> appendAll(K key, List<V> values) {
		List<V> existingValues = get(key);

		if (existingValues == null) {
			return super.put(key, values);
		} else {
			for (V val : values) {
				if (!existingValues.contains(val)) {
					existingValues.add(val);
				}
			}
			return existingValues;
		}

	}
	
	/**
	 * If an entry with <code>key</code> already exists, adds <code>value</code> to the associated list, checking if it's already present.<br />
	 * Otherwise, creates a new key-value pair. 
	 * @param key
	 * @param value
	 * @return resulting list associated with key
	 */
	public List<V> append(K key, V value) {
		List<V> values = get(key);
		
		if (values == null) {
			values = new LinkedList<V>();
			super.put(key, values);
		}
		
		if (!values.contains(value)) {
			values.add(value);
		}
		
		return values;
	}
}
