package me.sunmisc.transflow.inputs;

import java.io.InputStream;
import java.util.function.Supplier;

public class EnvelopeInput implements Input {

    private final Supplier<Input> input;

    public EnvelopeInput(Input input) {
        this(() -> input);
    }

    public EnvelopeInput(Supplier<Input> input) {
        this.input = input;
    }

    @Override
    public InputStream stream() throws Exception {
        return input.get().stream();
    }
}
