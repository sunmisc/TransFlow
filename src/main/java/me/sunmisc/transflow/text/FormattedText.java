package me.sunmisc.transflow.text;

public final class FormattedText extends EnvelopeCharSequence {
    public FormattedText(String format, Object... args) {
        super(() -> String.format(format, args));
    }
}
