package me.sunmisc.transflow.vk.requests;

import me.sunmisc.transflow.inputs.EnvelopeInput;
import me.sunmisc.transflow.inputs.Input;
import me.sunmisc.transflow.vk.TokenInput;
import me.sunmisc.transflow.vk.VkHead;

import java.util.List;

public class VkMethod extends EnvelopeInput
        implements Request, VkHead {

    public VkMethod(Input origin, String accessToken) {
        super(() -> new ConcatInput(List.of(
                origin,
                new TokenInput(accessToken)
        )));
    }

}
