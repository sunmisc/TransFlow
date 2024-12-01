package me.sunmisc.transflow.tls;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParseDirective implements Directive {
    private static final Pattern SPLIT = Pattern.compile(",\\s*");
    private final String identifier;
    private final String args;

    public ParseDirective(String identifier) {
        this(identifier, "");
    }
    public ParseDirective(String identifier, String args) {
        this.args = args;
        this.identifier = identifier;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Stream<String> arguments() {
        return SPLIT.splitAsStream(args);
    }

    @Override
    public Map<String, String> params() {
        return arguments()
                .map(x -> x.split("=", 2))
                .filter(x -> x.length == 2)
                .collect(Collectors.toUnmodifiableMap(
                        x -> x[0].trim(),
                        x -> x[1].trim().replaceAll("^\"|\"$", ""),
                        (x,y) -> x + "=" + y));
    }
}
