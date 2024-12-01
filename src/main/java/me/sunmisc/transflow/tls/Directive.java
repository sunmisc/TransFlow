package me.sunmisc.transflow.tls;

import java.util.Map;
import java.util.stream.Stream;

public interface Directive {

    String identifier();

    Stream<String> arguments();

    Map<String, String> params();
}
