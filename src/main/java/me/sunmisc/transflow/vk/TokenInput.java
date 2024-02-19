package me.sunmisc.transflow.vk;

import me.sunmisc.transflow.inputs.EnvelopeInput;
import me.sunmisc.transflow.io.QBytesInputStream;

import java.nio.charset.StandardCharsets;

public final class TokenInput extends EnvelopeInput {
    // todo: move it
    private static final String API_VERSION = "5.139";

    public TokenInput(String accessToken) {
        super(() -> new QBytesInputStream(String
                .format("&access_token=%s&v=%s", accessToken, API_VERSION)
                .getBytes(StandardCharsets.UTF_8)
        ));
    }

}