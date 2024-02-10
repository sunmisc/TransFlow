package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.inputs.Aes128TransportStream;
import me.sunmisc.transflow.inputs.Input;
import me.sunmisc.transflow.inputs.NetworkInput;
import me.sunmisc.transflow.tls.Playlist;
import me.sunmisc.transflow.tls.StartParsed;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public final class VEncryptedAudio implements Audio {

    private final Audio origin;

    public VEncryptedAudio(Audio origin) {
        this.origin = origin;
    }

    @Override
    public Optional<byte[]> stream() throws Exception {

        String url = Objects.requireNonNull(
                properties().get("url"),
                "url is null");

        URI baseUri = URI.create(url.substring(0, url.lastIndexOf("/") + 1));
        return new ConcatInput(
                origin.stream().map(input -> {
                    String data = new String(input, StandardCharsets.UTF_8);

                    Playlist playlist = new StartParsed(data.lines().toList());

                    return playlist.resources().map(resource -> {

                        Input src = new NetworkInput(baseUri.resolve(resource.uri()));
                        return resource.find("EXT-X-KEY").map(x -> {
                            Map<String, String> prop = x.params();
                            String method = prop.getOrDefault(
                                    "METHOD", "NONE");

                            if (method.equals("AES-128")) {

                                String keyUri = Objects.requireNonNull(
                                        prop.get("URI"),
                                        "public key not found");

                                return new Aes128TransportStream(src,
                                        new NetworkInput(URI.create(keyUri))
                                );
                            }
                            return src;
                        }).orElse(src);
                    }).toList();
                }).orElse(List.of())
        ).stream();
    }
    @Override
    public long id() {
        return origin.id();
    }

    @Override
    public String name() {
        return origin.name();
    }

    @Override
    public String author() {
        return origin.author();
    }

    @Override
    public int duration() {
        return origin.duration();
    }

    @Override
    public Map<String, String> properties() {
        return origin.properties();
    }
}
