package me.sunmisc.transflow.iterator;

import java.util.Iterator;
import java.util.function.Consumer;

public abstract class IteratorEnvelope<X> implements Iterator<X> {

    private final Iterator<? extends X> wrapped;

    public IteratorEnvelope(final Iterator<? extends X> iter) {
        this.wrapped = iter;
    }

    @Override
    public final boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public final X next() {
        return wrapped.next();
    }

    @Override
    public final void forEachRemaining(final Consumer<? super X> action) {
        wrapped.forEachRemaining(action);
    }

    @Override
    public final void remove() {
        wrapped.remove();
    }

    @Override
    public final String toString() {
        return wrapped.toString();
    }

    @Override
    public final boolean equals(final Object obj) {
        return wrapped.equals(obj);
    }

    @Override
    public final int hashCode() {
        return wrapped.hashCode();
    }
}
