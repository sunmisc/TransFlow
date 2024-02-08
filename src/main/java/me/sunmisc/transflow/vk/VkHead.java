package me.sunmisc.transflow.vk;

import java.util.stream.Stream;

public interface VkHead extends Head {

    @Override
    default Stream<Header> headers() {
        return Stream.of(
                new Header("User-Agent", "VKAndroidApp/7.35")
        );
    }
}
