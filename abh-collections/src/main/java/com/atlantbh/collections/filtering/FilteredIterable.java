package com.atlantbh.collections.filtering;

import java.util.Iterator;

/**
 * {@link Iterable} implementation that returns {@link FilteredIterator} for provided {@link Filter} 
 * @author Emir Arnautovic
 */
public class FilteredIterable<T> implements Iterable<T> {
	
	private final Iterable<T> unfilteredIterable;
	private final Filter<T> filter;

	public FilteredIterable(Iterable<T> unfilteredIterable, Filter<T> filter) {
		this.unfilteredIterable = unfilteredIterable;
		this.filter = filter;
	}

	@Override
	public Iterator<T> iterator() {
		return new FilteredIterator<T>(unfilteredIterable.iterator(), filter);
	}

}
