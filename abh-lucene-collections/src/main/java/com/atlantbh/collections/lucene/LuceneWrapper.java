package com.atlantbh.collections.lucene;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.atlantbh.utils.file.FileUtils;

public class LuceneWrapper implements Closeable {
	private final static Query SIZE_QUERY = new MatchAllDocsQuery();
	
	protected IndexWriter iw;
	protected DirectoryReader ir;
	protected IndexSearcher is;
	
	@SuppressWarnings("resource")
	public LuceneWrapper(boolean inMemory) {
		try {
			Directory dir;
			if (inMemory) {
				dir = new RAMDirectory();
			} else {
				dir = FSDirectory.open(FileUtils.getTempDirectory("LW-"));
			}
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_43, new SimpleAnalyzer(Version.LUCENE_43));
			
			iw = new IndexWriter(dir, conf);
			ir = DirectoryReader.open(iw, false);
			init(ir, iw);
		} catch (Exception e) {
			throw new LuceneCollectionException("Error creating index writer.", e);
		}
	}
	
	public LuceneWrapper(DirectoryReader ir, IndexWriter iw) {
		init(ir, iw);
	}
	
	protected void init(DirectoryReader ir, IndexWriter iw) {
		this.ir = ir;
		this.iw = iw;
		this.is = new IndexSearcher(ir);
	}
	
	protected boolean reloadIndexSearcher() {
		try {
			if (reloadReader()) {
				is = new IndexSearcher(ir);
				return true;
			}
		} catch (Exception e) {
			// wrap into runtime exception
			new LuceneCollectionException("Error opening index reader.", e);
		}
		return false;
	}
	
	protected DirectoryReader openReader() {
		try {
			return DirectoryReader.open(iw, true);
		} catch (Exception e) {
			// wrap into runtime exception
			throw new LuceneCollectionException("Error opening index reader", e);
		}
	}
	
	protected boolean reloadReader() {
		try {
			DirectoryReader newIr = DirectoryReader.openIfChanged(ir, iw, true);
			if (newIr != null && newIr != ir) {
				ir.close();
				ir = newIr;
				return true;
			}
		} catch (Exception e) {
			// wrap into runtime exception
			throw new LuceneCollectionException("Error opening index reader", e);
		}
		return false;
	}
	
	public void updateDocument(Term idTerm, Document doc) {
		try {
			iw.updateDocument(idTerm, doc);
		} catch (Exception e) {
			throw new LuceneCollectionException("Error updating document.", e);
		}
	}
	
	public void addDocument(Document doc) {
		try {
			iw.addDocument(doc);
		} catch (Exception e) {
			throw new LuceneCollectionException("Error adding new document.", e);
		}
	}
	
	public Document getDocument(Term term) {
		reloadReader();
		try {
			List<AtomicReaderContext> subreaders = ir.leaves();
			
			if (subreaders != null) {
				for (AtomicReaderContext arc : subreaders) {
					AtomicReader ar = arc.reader();
					DocsEnum docs = ar.termDocsEnum(term);
					if (docs != null && docs.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
						
						return ar.document(docs.docID());
					}
				}
			} 
		} catch (IOException e) {
			throw new LuceneCollectionException("Error retrieving document.", e);
		}
		return null;
		// TODO use index reader directly
	}
	
	public Document getDocument(Query query) {
		reloadIndexSearcher();
		try {
			TopDocs topRes = is.search(query, 1);
			if (topRes.totalHits > 0) {
				return is.doc(topRes.scoreDocs[0].doc);
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new LuceneCollectionException("Error retrieving document.", e);
		}
	}
	
	public void removeDocuments(Query query) {
		try {
			iw.deleteDocuments(query);
		} catch (IOException e) {
			throw new LuceneCollectionException("Error deleting documents by query.", e);
		}
	}
	
	public void removeAllDocuments() {
		try {
			iw.deleteAll();
		} catch (IOException e) {
			throw new LuceneCollectionException("Error deleting all documents.", e);
		}
	}
	
	
	public int size() {
		try {
			reloadIndexSearcher();
			return is.search(SIZE_QUERY, 1).totalHits;
		} catch (IOException e) {
			throw new LuceneCollectionException("Error evaluating map size.", e);
		}
	}
	
	@Override
	public void close() {
		// close reader
		try {
			if (ir != null) {
				ir.close();
				ir = null;
			}
		} catch (IOException e) {
			// ignore it
		}
		// close writer
		try {	
			if (iw != null) {
				iw.close();
				iw = null;
			}
		} catch (Exception e) {
			// ignore it
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}
}
