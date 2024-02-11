package me.sunmisc.transflow.inputs;

import me.sunmisc.transflow.util.Lazy;

import java.util.Optional;

public final class CachedInput implements Input {
    private final Lazy<byte[]> lazy;

    public CachedInput(Input origin) {
        this.lazy = new Lazy<>(() -> {
            try {
                return origin.stream().orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<byte[]> stream() throws Exception {
        return Optional.ofNullable(lazy.get());
    }
}
