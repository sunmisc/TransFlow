package me.sunmisc.transflow;

import me.sunmisc.transflow.inputs.Input;

import java.util.Map;

public interface Audio extends Input {

    long id();

    String name();

    String author();

    int duration();


    Map<String, String> properties();
}
