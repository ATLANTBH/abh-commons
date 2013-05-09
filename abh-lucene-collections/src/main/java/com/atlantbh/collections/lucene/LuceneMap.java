package com.atlantbh.collections.lucene;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

@SuppressWarnings("unchecked")
public class LuceneMap<K, V> extends LuceneWrapper implements Map<K, V> {
	protected boolean checkOnPut = true;
	protected KeyValueLuceneAdapter<K, V> adapter;

		
	public LuceneMap(KeyValueLuceneAdapter<K, V> adapter, boolean inMemory) {
		super(inMemory);
		this.adapter = adapter;
	}
	
	private LuceneMap(KeyValueLuceneAdapter<K, V> adapter, DirectoryReader ir, IndexWriter iw) {
		super(ir, iw);
		this.adapter = adapter;
	}
	
	public void setCheckOnPut(boolean checkOnPut) {
		this.checkOnPut = checkOnPut;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return getDocument(adapter.getKeyTerm((K)key)) != null;
	}

	@Override
	public V get(Object key) {
		Document doc = getDocument(adapter.getKeyTerm((K)key));
		if (doc != null) {
			return adapter.getValue(doc);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public V put(K key, V value) {
		V prevValue = null;
		if (checkOnPut) {
			prevValue = remove(key);
			addDocument(adapter.toDocument(key, value));
		} else {
			updateDocument(adapter.getKeyTerm(key), adapter.toDocument(key, value));
		}
		return prevValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		V prevValue = get(key);
		// remove previous if exists
		if (prevValue != null) {
			removeDocuments(adapter.getKeyQuery((K)key));
		}
		return prevValue;
	}
	
	@Override
	public void clear() {
		removeAllDocuments();
	}
	
	@Override
	public Set<K> keySet() {
		return new LuceneSet<K>(adapter.getKeyAdapter(), openReader(), iw);
	}

	@Override
	public Collection<V> values() {
		return new LuceneSet<V>(adapter.getValueAdapter(), openReader(), iw);
	}
	
	@Override
	public boolean containsValue(Object value) {
		// TODO add indexing support/check
		return values().contains(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException("LuceneMap does not support this operation - use iterator instead.");
	}
	
	public LuceneMap<K, V> getReadOnlyInstance() {
		return new LuceneMap<K, V>(adapter, ir, iw) {
			@Override
			protected boolean reloadReader() {
				return false;
			}
			
			@Override
			protected boolean reloadIndexSearcher() {
				return false;
			}
			
			@Override
			public void addDocument(Document doc) {
				throw new UnsupportedOperationException("Cannot change read only collection");
			}
			
			@Override
			public void updateDocument(Term idTerm, Document doc) {
				throw new UnsupportedOperationException("Cannot change read only collection");
			}
		};
	}
}
