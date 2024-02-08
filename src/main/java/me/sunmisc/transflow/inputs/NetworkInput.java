package me.sunmisc.transflow.inputs;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

public class NetworkInput implements Input {

    private final URI uri;

    public NetworkInput(URI uri) {
        this.uri = uri;
    }


    @Override
    public Optional<byte[]> stream() throws Exception {
        try (InputStream stream = uri.toURL().openStream()) {
            return Optional.of(stream.readAllBytes());
        }
    }
}
