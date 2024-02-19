package me.sunmisc.transflow.vk.requests;


import me.sunmisc.transflow.vk.Response;
import me.sunmisc.transflow.vk.Wire;

import java.io.InputStream;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class VkRequest implements Request {

    private final Supplier<Response> input;

    public VkRequest(Wire wire, Request input) {
        this.input = () -> {
            try {
                return wire.post(input);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


    @Override
    public InputStream stream() throws Exception {
        return input.get().stream();
    }

    @Override
    public Stream<Header> headers() throws Exception {
        return input.get().headers();
    }

}
