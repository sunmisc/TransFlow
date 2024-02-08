package me.sunmisc.transflow.vk;

import java.util.stream.Stream;

@FunctionalInterface
public interface Head {

    Stream<Header> headers() throws Exception;

    record Header(String name, String value) {}
}
