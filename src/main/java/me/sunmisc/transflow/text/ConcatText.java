package me.sunmisc.transflow.text;

import java.util.List;

public final class ConcatText extends EnvelopeCharSequence {

    public ConcatText(List<CharSequence> list) {
        super(() -> {
            StringBuilder builder = new StringBuilder();
            list.forEach(builder::append);
            return builder.toString();
        });
    }

    public ConcatText(List<CharSequence> list, CharSequence delimiter) {
        super(() -> String.join(delimiter, list));
    }

    public ConcatText(CharSequence delimiter, CharSequence... list) {
        super(() -> String.join(delimiter, list));
    }
}
