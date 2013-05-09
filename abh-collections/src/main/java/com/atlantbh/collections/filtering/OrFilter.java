package com.atlantbh.collections.filtering;

import java.util.LinkedList;


/**
 * Combines provided filters with logical OR - att least one must pass in order to accept object
 * @author Emir Arnautovic
 */
public class OrFilter<T> implements Filter<T> {
	LinkedList<Filter<T>> filters = new LinkedList<Filter<T>>();

	public void or(Filter<T> filter) {
		filters.add(filter);
	}
	
	@Override
	public boolean keep(T obj) {
		for (Filter<T> f : filters) {
			if (f.keep(obj)) {
				return true;
			}
		}
		return false;
	}

}
