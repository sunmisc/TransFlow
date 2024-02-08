package me.sunmisc.transflow;

import me.sunmisc.transflow.inputs.Input;

@FunctionalInterface
public interface Download<T extends Input> {

    void download(T input) throws Exception;

}
