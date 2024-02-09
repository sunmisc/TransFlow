package me.sunmisc.transflow.tls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseDirective implements Directive {
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
    public List<String> arguments() {
        return List.of(args.split(",\\s*"));
    }

    @Override
    public Map<String, String> params() {
        return arguments()
                .stream()
                .map(x -> x.split("=", 2))
                .filter(x -> x.length == 2)
                .collect(Collectors.toUnmodifiableMap(
                        x -> x[0].trim(),
                        x -> x[1].trim().replaceAll("^\"|\"$", ""),
                        (x,y) -> x + "=" + y));
    }
}
