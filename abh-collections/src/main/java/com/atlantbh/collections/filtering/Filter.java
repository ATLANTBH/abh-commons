package com.atlantbh.collections.filtering;

/**
 * Interface used for evaluating objects when filtering
 * 
 * @author Emir Arnautovic
 *
 * @param <T> the type of object that is evaluated
 */
public interface Filter<T> {
	boolean keep(T obj);
}
