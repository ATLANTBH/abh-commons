package com.atlantbh.collections.filtering;


/**
 * Negates provided filter.
 * @author Emir Arnautovic
 */
public class NotFilter<T> implements Filter<T> {
	
	private final Filter<T> yesFilter;

	public NotFilter(Filter<T> yesFilter) {
		this.yesFilter = yesFilter;	
	}

	@Override
	public boolean keep(T obj) {
		return !yesFilter.keep(obj);
	}

}
