package com.navteq.mapcompiler.standalone.util.lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.atlantbh.collections.lucene.LuceneAdapter;
import com.atlantbh.collections.lucene.LuceneDocumentIterator;


public class LuceneDocumentIteratorTest {
	
	private LuceneDocumentIterator getIterator(int count, int... deleteId) throws Exception {
		Directory dir = new RAMDirectory();
		IndexWriter iw = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_43, new SimpleAnalyzer(Version.LUCENE_43)));
		for (int i=0; i<count; i++) {
			Document doc = new Document();
			doc.add(new Field("id", "id" + i, LuceneAdapter.LOOKUP_FIELD_TYPE));
			doc.add(new Field("val", "val" + i, LuceneAdapter.RETRIEVABLE_LOOKUP_FIELD_TYPE));
			iw.addDocument(doc);
		}
		for (int i=0; i<deleteId.length; i++) {
			iw.deleteDocuments(new Term("id", "id" + deleteId[i]));
		}
		iw.commit();
		iw.close();
		
		LuceneDocumentIterator docs = new LuceneDocumentIterator(DirectoryReader.open(dir));
		return docs;
	}
	
	@Test
	public void testEmptyIterator() throws Exception {
		LuceneDocumentIterator iter = getIterator(0);
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testIterator() throws Exception {
		LuceneDocumentIterator iter = getIterator(10);
		int cnt = 0;
		while (iter.hasNext()) {
			Document doc = iter.next();
			assertEquals("val" + cnt, doc.get("val"));
			cnt++;
		}
		assertEquals(10, cnt);
	}
	
	@Test
	public void testIteratorWithDeletedDocuments() throws Exception {
		LuceneDocumentIterator iter = getIterator(10, 2, 4);
		int cnt = 0;
		while (iter.hasNext()) {
			Document doc = iter.next();
			assertNotSame("val2", doc.get("val"));
			assertNotSame("val4", doc.get("val"));
			cnt++;
		}
		assertEquals(8, cnt);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemove() throws Exception {
		LuceneDocumentIterator iter = getIterator(10);
		while (!iter.next().get("val").equals("val2")){
			// iterate
		}
		iter.remove();
	}
}
