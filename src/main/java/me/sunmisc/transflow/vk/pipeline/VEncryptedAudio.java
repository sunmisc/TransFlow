package me.sunmisc.transflow.vk.pipeline;

import me.sunmisc.transflow.Audio;
import me.sunmisc.transflow.Author;
import me.sunmisc.transflow.inputs.Aes128TransportStream;
import me.sunmisc.transflow.inputs.Input;
import me.sunmisc.transflow.inputs.NetworkInput;
import me.sunmisc.transflow.tls.Playlist;
import me.sunmisc.transflow.tls.StartParsed;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class VEncryptedAudio implements Audio {

    private final Audio origin;

    public VEncryptedAudio(Audio origin) {
        this.origin = origin;
    }

    @Override
    public InputStream stream() throws Exception {

        String url = Objects.requireNonNull(
                properties().get("url"),
                "url is null");

        URI baseUri = URI.create(url.substring(0, url.lastIndexOf("/") + 1));


        try (InputStream o = origin.stream()) {
            String data = new String(o.readAllBytes(), StandardCharsets.UTF_8);

            Playlist playlist = new StartParsed(data.lines().toList());

            return new ConcatInput(playlist
                    .resources()
                    .map(resource -> {
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
                    })
                    .toList()
            ).stream();
        }
    }
    @Override
    public long id() {
        return origin.id();
    }

    @Override
    public CharSequence name() {
        return origin.name();
    }

    @Override
    public Stream<Author> authors() {
        return origin.authors();
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
