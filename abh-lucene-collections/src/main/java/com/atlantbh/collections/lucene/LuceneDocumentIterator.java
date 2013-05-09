package com.atlantbh.collections.lucene;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.util.Bits;

public class LuceneDocumentIterator implements Iterator<Document> {
	private final IndexReader ir;
	
	private int currentDocNum;
	private Document currentDoc;
	private int nextDocNum;
	private Document nextDoc;
	
	private int maxDoc;

	private Bits liveDocs;

	public LuceneDocumentIterator(IndexReader ir) {
		this.ir = ir;
		// TODO Check alternative way of doing deletion check since it is noted to be slow
		liveDocs = MultiFields.getLiveDocs(ir);
		maxDoc = ir.maxDoc()-1;
		currentDocNum = -1;
		currentDoc = null;
		nextDocNum = -1;
		nextDoc = null;
	}

	@Override
	public boolean hasNext() {
		while(nextDocNum < maxDoc && nextDoc == null) {
			try {
				nextDocNum++;
				if (liveDocs == null || liveDocs.get(nextDocNum)) {
					nextDoc = ir.document(nextDocNum);
				}
				
			} catch (Exception e) {
				throw new LuceneCollectionException("Error retrieving document by docId.", e);
			}
		}
		return nextDoc != null;
	}

	@Override
	public Document next() {
		if (hasNext()) {
			currentDoc = nextDoc;
			currentDocNum = nextDocNum;
			
			nextDoc = null;
			return currentDoc;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot delete document " + currentDocNum + " - LuceneDocumentIterator is readonly - use AbstractLuceneWrapper.removeDocuments(Query)");
	}
	
	public IndexReader getIndexReader() {
		return ir;
	}

}
