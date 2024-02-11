package me.sunmisc.transflow.vk.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import me.sunmisc.transflow.Author;

public final class VAuthor implements Author {

    private final JsonNode node;

    public VAuthor(JsonNode node) {
        this.node = node;
    }

    @Override
    public String identifier() {
        return node.path("id").asText();
    }

    @Override
    public String name() {
        return node.path("name").asText("Unknown");
    }
}
