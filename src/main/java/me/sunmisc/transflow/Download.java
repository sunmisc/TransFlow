package me.sunmisc.transflow;

import me.sunmisc.transflow.inputs.Input;

public interface Download<T extends Input> {

    void download(T input) throws Exception;

}
