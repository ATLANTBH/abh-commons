package com.navteq.mapcompiler.standalone.util.lucene;

import static junit.framework.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlantbh.collections.lucene.KeyValueLuceneAdapter;
import com.atlantbh.collections.lucene.LuceneMap;

public class LuceneMapTest {
	
	private LuceneMap<String, String> map;

	@Before
	public void initMap() {
		map = new LuceneMap<String, String>(new KeyValueLuceneAdapter<String, String>(), true);
	}
	
	@Test
	public void testPut() {
		assertEquals(0, map.size());
		assertNull(map.put("a", "1"));
		assertEquals(1, map.size());
		
		// close to delete files
		map.close();
	}
	
	@Test
	public void testGet() {
		map.put("a", "1");

		assertEquals("1", map.get("a"));
		assertNull(map.get("b"));
	}
	
	@Test
	public void testOverwriteValue() {
		map.put("a", "1");
		// add value with same key - prev returned
		assertEquals("1", map.put("a", "2"));
		assertEquals(1, map.size());
		
		assertEquals("2", map.get("a"));
	}
	
	@Test
	public void testRemove() {
		map.put("a", "1");
		// add value with same key
		assertEquals("1", map.remove("a"));
		assertEquals(0, map.size());
		assertNull(map.get("1"));
		// null since already deleted
		assertNull(map.remove("1"));
	}
	
	@Test
	public void testSize() {
		for (int i=0; i<10; i++) {
			map.put("k" + i, "v" + i);
			assertEquals(i+1, map.size());
		}
	}
	
	@Test
	public void testIsEmpty() {
		assertTrue(map.isEmpty());
		map.put("a", "1");
		assertFalse(map.isEmpty());
	}
	
	@Test
	public void testKeySet() {
		for (int i=0; i<10; i++) {
			map.put("k" + i, "v" + i);
		}
		Set<String> keys = map.keySet();
		assertEquals(map.size(), keys.size());
	}
}
