package me.sunmisc.transflow.tls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseDirective implements Directive {
    private final String args;
    private final String identifier;

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
    public List<String> arguments() {
        return List.of(args.split(","));
    }

    @Override
    public Map<String, String> params() {
        return arguments()
                .stream()
                .map(x -> x.split("="))
                .filter(x -> x.length == 2)
                .collect(Collectors.toUnmodifiableMap(
                        x -> x[0],
                        x -> x[1],
                        (x,y) -> x + "=" + y));
    }
}
