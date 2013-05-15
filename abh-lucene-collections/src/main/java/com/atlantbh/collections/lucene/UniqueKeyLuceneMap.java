package com.atlantbh.collections.lucene;

public class UniqueKeyLuceneMap<K, V> extends LuceneMap<K, V> {

	public UniqueKeyLuceneMap(KeyValueLuceneAdapter<K, V> adapter, boolean inMemory) {
		super(adapter, inMemory);
	}
	
	@Override
	public V put(K key, V value) {
		updateDocument(adapter.getKeyTerm(key), adapter.toDocument(key, value));
		return null;
	}
	

}
