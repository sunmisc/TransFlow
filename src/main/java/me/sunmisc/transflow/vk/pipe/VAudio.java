package me.sunmisc.transflow.vk.pipe;

import com.fasterxml.jackson.databind.JsonNode;
import me.sunmisc.transflow.Audio;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class VAudio implements Audio {
    private final JsonNode node;

    public VAudio(JsonNode node) {
        this.node = node;
    }

    @Override
    public Optional<byte[]> stream() throws Exception {
        String url = node.findValue("url").asText();
        if (url == null || url.isEmpty())
            return Optional.empty();
        try (InputStream bs = URI.create(url).toURL().openStream()) {
            return Optional.of(bs.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("failed url: " + url);
        }
    }
    @Override
    public long id() {
        return node.findValue("id").asLong();
    }

    @Override
    public String name() {
        return node.findValue("title").asText();
    }

    @Override
    public String author() {
        return node.findValue("artist").asText();
    }

    @Override
    public int duration() {
        return node.findValue("duration").asInt();
    }

    @Override
    public Map<String, String> properties() {
        return Map.of(
                "url", node.findValue("url").asText()
        );
    }
}
