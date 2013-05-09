package com.atlantbh.collections.lucene;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

public class KeyValueLuceneAdapter<K, V> extends LuceneAdapter<Entry<K, V>> {

	protected static final String VALUE_FIELD = "val";
	protected static final String KEY_FIELD = "key";
	
	private static final FieldType KEY_FIELD_TYPE = LOOKUP_FIELD_TYPE;
	private static final FieldType VALUE_FIELD_TYPE = new FieldType();
	
	static {
		VALUE_FIELD_TYPE.setIndexed(false);
		VALUE_FIELD_TYPE.setStored(true);
	}

	public KeyValueLuceneAdapter() {
		super(null, null);
	}
	
	public Document toDocument(K key, V val) {
		Document doc = new Document();
		addField(doc, KEY_FIELD, key, KEY_FIELD_TYPE);
		// add non-null
		if (val != null) {
			addField(doc, VALUE_FIELD, val, VALUE_FIELD_TYPE);
		}
		return doc;
	}
	
	@Override
	public Document toDocument(Entry<K, V> obj) {
		return toDocument(obj.getKey(), obj.getValue());
	}
		
	@Override
	public Query getUniquenessQuery(Entry<K, V> obj) {
		return getKeyQuery(obj.getKey());
	}
	
	@Override
	public Entry<K, V> toObject(Document doc) {
		return new AbstractMap.SimpleEntry<K, V>(getKey(doc), getValue(doc));
	}
	
	@SuppressWarnings("unchecked")
	public K getKey(Document doc) {
		return (K)getFieldValue(doc, KEY_FIELD);
	}
	
	@SuppressWarnings("unchecked")
	public V getValue(Document doc) {
		return (V)getFieldValue(doc, VALUE_FIELD);
	}
	
	public Query getKeyQuery(K key) {
		return getQuery(KEY_FIELD, key);
	}
	
	public Term getKeyTerm(K key) {
		return getTerm(KEY_FIELD, key);
	}
	
	public LuceneAdapter<K> getKeyAdapter() {
		return new LuceneAdapter<K>(KEY_FIELD, KEY_FIELD_TYPE);
	}
	
	public LuceneAdapter<V> getValueAdapter() {
		return new LuceneAdapter<V>(VALUE_FIELD, VALUE_FIELD_TYPE);
	}
}
