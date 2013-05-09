package com.atlantbh.collections.lucene;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;

public class LuceneSet<E> extends LuceneWrapper implements Set<E> {
	private final LuceneAdapter<E> adapter;
	
	public LuceneSet(LuceneAdapter<E> adapter) {
		super(true);
		this.adapter = adapter;
	}

	public LuceneSet(LuceneAdapter<E> adapter, DirectoryReader ir, IndexWriter iw) {
		super(ir, iw);
		this.adapter = adapter;
	}

	@Override
	public boolean add(E e) {
		remove(e);
		addDocument(adapter.toDocument(e));
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			changed |= add(e);
		}
		return changed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		return getDocument(adapter.getUniquenessQuery((E) o)) != null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}	

	@Override
	public boolean isEmpty() {
		return size() > 0;
	}
	
	@Override
	public void clear() {
		removeAllDocuments();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		boolean deleted = contains(o);
		removeDocuments(adapter.getUniquenessQuery((E) o));
		return deleted;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
        boolean modified = false;

        if (size() > c.size()) {
            for (Iterator<?> i = c.iterator(); i.hasNext(); )
                modified |= remove(i.next());
        } else {
            for (Iterator<?> i = iterator(); i.hasNext(); ) {
                if (c.contains(i.next())) {
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<E> e = iterator();
		while (e.hasNext()) {
			if (!c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public Iterator<E> iterator() {
		return new LuceneIterator<E>(ir, adapter);
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Loading entire index in memory is not allowed - use iterator instead.");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Loading entire index in memory is not allowed - use iterator instead.");
	}
}
