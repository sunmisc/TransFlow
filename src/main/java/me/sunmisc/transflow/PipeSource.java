package me.sunmisc.transflow;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface PipeSource extends Spliterator<Audio> {


    default void allForEach(Consumer<Audio> action) {
        Objects.requireNonNull(action);
        Spliterator<Audio> src = this;
        do {
            src.forEachRemaining(action);
        } while ((src = src.trySplit()) != null);
    }

    class Empty implements PipeSource {

        @Override
        public boolean tryAdvance(Consumer<? super Audio> action) {
            return false;
        }

        @Override
        public Spliterator<Audio> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }
}
