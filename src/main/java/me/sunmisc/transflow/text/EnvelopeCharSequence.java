package me.sunmisc.transflow.text;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public class EnvelopeCharSequence implements CharSequence {

    private final Supplier<CharSequence> value;

    public EnvelopeCharSequence(Supplier<CharSequence> value) {
        this.value = value;
    }

    @Override
    public final int length() {
        return value.get().length();
    }

    @Override
    public final char charAt(int i) {
        return value.get().charAt(i);
    }

    @Override
    public final boolean isEmpty() {
        return value.get().isEmpty();
    }

    @Override
    public final CharSequence subSequence(int i, int i1) {
        return value.get().subSequence(i,i1);
    }

    @Override
    public final IntStream chars() {
        return value.get().chars();
    }

    @Override
    public final IntStream codePoints() {
        return value.get().codePoints();
    }

    @Override
    public final boolean equals(Object o) {
        return value.get().equals(o);
    }

    @Override
    public final int hashCode() {
        return value.get().hashCode();
    }

    @Override
    public final String toString() {
        return value.get().toString();
    }
}
