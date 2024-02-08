package me.sunmisc.transflow.inputs;

import me.sunmisc.transflow.util.Lazy;

import java.util.Optional;

public class CachedInput implements Input {
    private final Lazy<byte[]> cached;

    public CachedInput(Input origin) {
        this.cached = new Lazy<>(() -> {
            try {
                return origin.stream().orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Optional<byte[]> stream() throws Exception {
        return Optional.ofNullable(cached.get());
    }
}
