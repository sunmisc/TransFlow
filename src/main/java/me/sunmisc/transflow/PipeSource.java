package me.sunmisc.transflow;

import me.sunmisc.transflow.inputs.Input;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public interface PipeSource<E extends Input> extends Spliterator<E> {


    default void allForEach(Consumer<E> action) {
        Objects.requireNonNull(action);
        Spliterator<E> src = this;
        do {
            src.forEachRemaining(action);
        } while ((src = src.trySplit()) != null);
    }
}
