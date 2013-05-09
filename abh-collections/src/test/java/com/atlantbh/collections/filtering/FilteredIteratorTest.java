package com.atlantbh.collections.filtering;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.atlantbh.collections.filtering.AndFilter;
import com.atlantbh.collections.filtering.Filter;
import com.atlantbh.collections.filtering.FilteredIterator;
import com.atlantbh.collections.filtering.OrFilter;

import static org.junit.Assert.*;


@SuppressWarnings("serial")
public class FilteredIteratorTest {
	int numVals = 20;
	LinkedList<String> vals = new LinkedList<String>(){{
		for (int i=0; i<numVals; i++) {
			add(String.valueOf(i));
		}
	}};
	Iterator<String> iterator;
	
	@Before
	public void initIterator() {
		iterator = vals.iterator();
	}
	
	@Test
	public void testEmptyIterator() {
		FilteredIterator<String> empty = new FilteredIterator<String>(new LinkedList<String>().iterator(), new Filter<String>() {

			@Override
			public boolean keep(String obj) {
				return true;
			}});
		
		assertFalse(empty.hasNext());
	}
	
	@Test
	public void testNoFiltering() {
		FilteredIterator<String> noFilter = new FilteredIterator<String>(iterator, new Filter<String>() {

			@Override
			public boolean keep(String obj) {
				return true;
			}});
		
		assertTrue(noFilter.hasNext());
		int cnt = 0;
		while (noFilter.hasNext()) {
			String val = noFilter.next();
			assertEquals(String.valueOf(cnt), val);
			cnt++;
		}
		assertEquals(numVals, cnt);
	}
	
	@Test
	public void testFullFiltering() {
		FilteredIterator<String> fullFilter = new FilteredIterator<String>(iterator, new Filter<String>() {

			@Override
			public boolean keep(String obj) {
				return false;
			}});
		
		assertFalse(fullFilter.hasNext());
	}
	
	@Test
	public void testFullFilteringNext() {
		FilteredIterator<String> fullFilter = new FilteredIterator<String>(iterator, new Filter<String>() {

			@Override
			public boolean keep(String obj) {
				return false;
			}});
		try {
			fullFilter.next();
			fail("NoSuchElementException expected");
		} catch (NoSuchElementException e) {
			// expected exception
		}
	}
	
	@Test
	public void testFilterFirstValues() {
		// filter first
		FilteredIterator<String> iter = new FilteredIterator<String>(iterator, new ValueFilter(toSet("0")));
		assertTrue(iter.hasNext());
		assertEquals("1", iter.next());
		// filter first three
		iter = new FilteredIterator<String>(iterator, new ValueFilter(toSet("0", "1", "2")));
		assertEquals("3", iter.next());
	}
	
	
	@Test
	public void testFilterMiddleValues() {
		Set<String> filtered = toSet("3", "6", "7", "18");
		// filter first
		FilteredIterator<String> iter = new FilteredIterator<String>(iterator, new ValueFilter(filtered));
		assertEquals("0", iter.next());
		int cnt = 1;
		while (iter.hasNext()) {
			String val = iter.next();
			assertFalse(filtered.contains(val));
			cnt++;
		}
		assertEquals(numVals-filtered.size(), cnt);
	}
	
	@Test
	public void testFilterLastValue() {
		// filter first
		FilteredIterator<String> iter = new FilteredIterator<String>(iterator, new ValueFilter(toSet(String.valueOf(numVals-1))));
		int cnt = 0;
		while (iter.hasNext()) {
			String val = iter.next();
			assertEquals(String.valueOf(cnt), val);
			cnt++;
		}
		assertEquals(numVals-1, cnt);
	}
	
	@Test
	public void testAndFilter() {
		Set<String> filtered1 = toSet("3", "18");
		Set<String> filtered2 = toSet("3" ,"6");
		
		AndFilter<String> andFilter = new AndFilter<String>();
		andFilter.and(new ValueFilter(filtered1, false));
		andFilter.and(new ValueFilter(filtered2, false));
		
		FilteredIterator<String> iter = new FilteredIterator<String>(iterator, andFilter);
		assertEquals("3", iter.next());
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testOrFilter() {
		Set<String> filtered1 = toSet("3", "18");
		Set<String> filtered2 = toSet("3" ,"6");
		
		OrFilter<String> orFilter = new OrFilter<String>();
		orFilter.or(new ValueFilter(filtered1, false));
		orFilter.or(new ValueFilter(filtered2, false));
		
		FilteredIterator<String> iter = new FilteredIterator<String>(iterator, orFilter);
		assertEquals("3", iter.next());
		assertEquals("6", iter.next());
		assertEquals("18", iter.next());
		assertFalse(iter.hasNext());
	}
	
	
	private Set<String> toSet(String...filtered) {
		Set<String> set = new HashSet<String>();
		for (String val : filtered) {
			set.add(val);
		} 
		return set;
	}
	
	private class ValueFilter implements Filter<String> {
		private final Set<String> values;
		private final boolean skipVals;
		
		public ValueFilter(Set<String> vals) {
			this(vals, true);
		}

		public ValueFilter(Set<String> vals, boolean skipVals) {
			this.values = vals;
			this.skipVals = skipVals;
		}
		
		@Override
		public boolean keep(String obj) {
			return skipVals ? !values.contains(obj) : values.contains(obj);
		}
		
	}
}
