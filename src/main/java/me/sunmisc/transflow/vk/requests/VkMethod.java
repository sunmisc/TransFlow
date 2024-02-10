package me.sunmisc.transflow.vk.requests;

import me.sunmisc.transflow.inputs.EnvelopeInput;
import me.sunmisc.transflow.inputs.Input;
import me.sunmisc.transflow.vk.TokenInput;

import java.util.List;
import java.util.stream.Stream;

public final class VkMethod extends EnvelopeInput
        implements Request {

    public VkMethod(Input origin, String accessToken) {
        super(() -> new ConcatInput(List.of(
                origin,
                new TokenInput(accessToken)
        )));
    }

    @Override
    public Stream<Header> headers() {
        return Stream.of(
                new Header("User-Agent", "VKAndroidApp/7.35")
        );
    }
}
