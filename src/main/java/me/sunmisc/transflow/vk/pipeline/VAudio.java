package me.sunmisc.transflow.vk.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Author;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class VAudio implements Audio {
    private final JsonNode node;

    public VAudio(JsonNode node) {
        this.node = node;
    }

    @Override
    public InputStream stream() throws Exception {
        String url = node.findValue("url").asText();
        if (url == null || url.isEmpty())
            return InputStream.nullInputStream();
        return URI.create(url).toURL().openStream();
    }
    @Override
    public long id() {
        return node.findValue("id").asLong();
    }

    @Override
    public Stream<Author> authors() {
        JsonNode main = node.path("main_artists");
        JsonNode feat = node.path("featured_artists");

        final boolean p = false;

        return Stream.concat(
                StreamSupport.stream(main.spliterator(), p),
                StreamSupport.stream(feat.spliterator(), p)
        ).map(VAuthor::new);
    }

    @Override
    public CharSequence name() {
        return node.findValue("title").asText();
    }

    @Override
    public int duration() {
        return node.findValue("duration").asInt();
    }

    @Override
    public Map<String, String> properties() {
        return Map.of(
                "url", node
                        .findValue("url")
                        .asText(),
                "album", node
                        .path("album")
                        .path("title")
                        .asText("Unknown")
        );
    }
}
