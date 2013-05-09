package com.atlantbh.collections.filtering;

import java.util.LinkedList;

/**
 * Combines provided filters with logical AND - all must pass in order to accept object
 * @author Emir Arnautovic
 *
 * @param <T> the type of object that is evaluated
 */
public class AndFilter<T> implements Filter<T> {
	LinkedList<Filter<T>> filters = new LinkedList<Filter<T>>();

	public void and(Filter<T> filter) {
		filters.add(filter);
	}
	
	@Override
	public boolean keep(T obj) {
		for (Filter<T> f : filters) {
			if (!f.keep(obj)) {
				return false;
			}
		}
		return true;
	}

}
