package me.sunmisc.transflow.vk;

import me.sunmisc.transflow.inputs.Input;

public interface Response extends Input, Head {

    int status() throws Exception;
}
