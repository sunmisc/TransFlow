package me.sunmisc.transflow.inputs;

import java.io.InputStream;
import java.net.URI;

public final class NetworkInput implements Input {

    private final URI uri;

    public NetworkInput(URI uri) {
        this.uri = uri;
    }

    @Override
    public InputStream stream() throws Exception {
        return uri.toURL().openStream();
    }
}
