package me.sunmisc.transflow.vk.requests;


import me.sunmisc.transflow.inputs.Input;
import me.sunmisc.transflow.io.QBytesInputStream;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class MappedRequest implements Input {

    private final Input origin;

    public MappedRequest(Input origin) {
        this.origin = origin;
    }
    public MappedRequest(Map<String, Object> map) {
        this(() -> new QBytesInputStream(map
                .entrySet()
                .stream()
                .map(e -> {
                    String key = e.getKey();
                    Object val = e.getValue();

                    return key + "=" + URLEncoder.encode(
                            requireNonNull(val).toString(),
                            StandardCharsets.UTF_8);
                }).collect(joining("&"))
                .getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public InputStream stream() throws Exception {
        return origin.stream();
    }
}
