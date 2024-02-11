package me.sunmisc.transflow.text;

import java.util.regex.Pattern;

public final class FilenameNormalized extends EnvelopeCharSequence {

    private static final Pattern NORMALIZED =
            Pattern.compile("[/\\\\?%*:|\"<>]");

    public FilenameNormalized(CharSequence origin) {
        super(() -> NORMALIZED
                .matcher(origin)
                .replaceAll("|"));

    }
}
