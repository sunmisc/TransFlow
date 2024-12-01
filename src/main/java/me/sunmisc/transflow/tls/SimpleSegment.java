package me.sunmisc.transflow.tls;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class SimpleSegment implements Segment {

    private final Map<String, Directive> directives
            = new LinkedHashMap<>();
    private final URI uri;

    public SimpleSegment(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public Stream<Directive> directives() {
        return directives.values().stream();
    }

    @Override
    public Optional<Directive> find(String name) {
        return Optional.ofNullable(directives.get(name));
    }

    @Override
    public void add(Directive directive) {
        directives.put(directive.identifier(), directive);
    }
}
