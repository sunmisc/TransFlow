package me.sunmisc.transflow.tls;

import java.util.Map;
import java.util.stream.Stream;

public interface Playlist {

    Stream<Segment> resources();

    Map<String, Directive> metadata();
}
