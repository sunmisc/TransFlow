package me.sunmisc.transflow.iterator;

import java.util.Enumeration;
import java.util.Iterator;

public final class EnumerationAsIterator<E> implements Enumeration<E> {
    private final Iterator<E> itr;

    public EnumerationAsIterator(Iterator<E> itr) {
        this.itr = itr;
    }
    @Override
    public boolean hasMoreElements() {
        return itr.hasNext();
    }
    @Override
    public E nextElement() {
        return itr.next();
    }
}
