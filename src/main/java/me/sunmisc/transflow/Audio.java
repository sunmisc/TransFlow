package me.sunmisc.transflow;

import me.sunmisc.transflow.inputs.Input;

import java.util.Map;
import java.util.stream.Stream;

public interface Audio extends Input {

    long id();

    Stream<Author> authors();


    CharSequence name();

    int duration();


    Map<String, String> properties();
}
