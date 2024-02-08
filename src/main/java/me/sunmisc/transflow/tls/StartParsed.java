package me.sunmisc.transflow.tls;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

public class StartParsed implements Playlist {

    private static final String END_TOKEN = "EXT-X-ENDLIST";
    private final List<String> lines;

    public StartParsed(List<String> lines) {
        this.lines = lines;
    }


    @Override
    public Stream<Segment> resources() {
        List<Segment> segments = new LinkedList<>();

        List<Directive> accumulate = new LinkedList<>();

        for (String line : lines) {
            if (line.charAt(0) == '#') {
                line = line.substring(1);

                int q = line.indexOf(':');
                String name = q < 0 ? line : line.substring(0, q);
                String arg =  q < 0 ? ""   : line.substring(q + 1);

                if (name.equals(END_TOKEN)) break;
                accumulate.add(new ParseDirective(name, arg));
            } else {
                Segment segment = new SimpleSegment(URI.create(line));

                accumulate.forEach(segment::add);

                segments.add(segment);

                accumulate.clear();
            }
        }
        return Collections.unmodifiableList(segments).stream();
    }

    @Override
    public Map<String, Directive> metadata() {
        Map<String, Directive> metadata = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("#EXT-X-")) {

                line = line.substring(1);

                if (line.equals(END_TOKEN)) break;

                int q = line.indexOf(':');
                String name = q < 0 ? line : line.substring(0, q);
                String arg =  q < 0 ? ""   : line.substring(q + 1);

                metadata.putIfAbsent(name, new ParseDirective(name, arg));
            }
        }
        return Collections.unmodifiableMap(metadata);
    }
}
