package com.atlantbh.collections.lucene;

public class MultivalueLuceneMap<K, V> extends LuceneMap<K, V> {
	public MultivalueLuceneMap(KeyValueLuceneAdapter<K, V> adapter, boolean inMemory) {
		super(adapter, inMemory);
	}
	
	@Override
	public V put(K key, V value) {
		addDocument(adapter.toDocument(key, value));
		return null;
	}
}
