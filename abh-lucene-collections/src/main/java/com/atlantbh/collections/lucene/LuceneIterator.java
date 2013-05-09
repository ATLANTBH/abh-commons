package com.atlantbh.collections.lucene;

import java.util.Iterator;

import org.apache.lucene.index.IndexReader;

public class LuceneIterator<E> implements Iterator<E> {
	private final LuceneDocumentIterator docs;
	private final LuceneAdapter<E> adapter;
	
	public LuceneIterator(IndexReader ir, LuceneAdapter<E> adapter) {
		this.adapter = adapter;
		docs = new LuceneDocumentIterator(ir);
	}

	@Override
	public boolean hasNext() {
		return docs.hasNext();
	}

	@Override
	public E next() {
		return adapter.toObject(docs.next());
	}

	@Override
	public void remove() {
		docs.remove();
	}

}
