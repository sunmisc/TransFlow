package me.sunmisc.transflow.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public final class MappedIterator<Y> extends IteratorEnvelope<Y> {
    public <X> MappedIterator(
            final Function<? super X, ? extends Y> func,
            final Iterator<? extends X> iterator) {
        super(
                new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Y next() {
                        if (hasNext())
                            return func.apply(iterator.next());
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                }
        );
    }

}
