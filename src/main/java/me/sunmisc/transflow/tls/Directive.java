package me.sunmisc.transflow.tls;

import java.util.List;
import java.util.Map;

public interface Directive {

    String identifier();

    List<String> arguments();

    Map<String, String> params();
}
