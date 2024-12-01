package me.sunmisc.transflow.tls;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

public interface Segment {

    URI uri();

    Stream<Directive> directives();

    Optional<Directive> find(String name);

    void add(Directive directive);



}
