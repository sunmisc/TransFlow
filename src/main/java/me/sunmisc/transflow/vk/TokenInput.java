package me.sunmisc.transflow.vk;

import me.sunmisc.transflow.inputs.EnvelopeInput;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TokenInput extends EnvelopeInput {
    private static final String API_VERSION = "5.139";

    public TokenInput(String accessToken) {
        super(() -> Optional.of(String
                .format("&access_token=%s&v=%s", accessToken, API_VERSION)
                .getBytes(StandardCharsets.UTF_8)
        ));
    }

}