package com.atlantbh.collections.filtering;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} implementation that applies provided {@link Filter} on unfiltered {@link Iterator}
 * @author Emir Arnautovic
 */
public class FilteredIterator<T> implements Iterator<T> {
	private final Iterator<T> unfilteredIterator;
	private final Filter<T> filter;
	
	private T next;

	public FilteredIterator(Iterator<T> unfilteredIterator, Filter<T> filter) {
		this.unfilteredIterator = unfilteredIterator;
		this.filter = filter;
	}
	
	@Override
	public boolean hasNext() {
		while (next == null && unfilteredIterator.hasNext()) {
			next = filter(unfilteredIterator.next());
		}
		return next != null;
	}

	@Override
	public T next() {
		if (hasNext()) {
			T toReturn = next;
			next = null;
			return toReturn;
		}
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Unsupported operation since this implementation is peeking underlying iterator.");
	}
	
	protected T filter(T obj) {
		return filter.keep(obj) ? obj : null;
	}
}
