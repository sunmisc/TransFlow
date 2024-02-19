package me.sunmisc.transflow.inputs;


import me.sunmisc.transflow.iterator.EnumerationAsIterator;
import me.sunmisc.transflow.iterator.MappedIterator;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;

@FunctionalInterface
public interface Input {

    InputStream stream() throws Exception;

    class ConcatInput implements Input {

        private final List<? extends Input> inputs;

        public ConcatInput(List<? extends Input> inputs) {
            this.inputs = inputs;
        }
        @Override
        public InputStream stream() throws Exception {
            return new SequenceInputStream(
                    new EnumerationAsIterator<>(new MappedIterator<>(o -> {
                        try {
                            return o.stream();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, inputs.iterator()))
            );
        }
    }
}
