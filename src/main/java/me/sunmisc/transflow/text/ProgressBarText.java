package me.sunmisc.transflow.text;

public final class ProgressBarText extends EnvelopeCharSequence {
    private static final int TOTAL_PROGRESS = 100;
    private static final int BAR_WIDTH = 50;
    private static final String FORMAT
            = "\r[%s] %s%%";

    public ProgressBarText(Number value) {
        super(() -> {
            int aw = BAR_WIDTH;

            double currentProgress = value.doubleValue();

            int cw = (int) (aw * (currentProgress / TOTAL_PROGRESS));

            return new FormattedText(FORMAT,
                    "=".repeat(cw) + " ".repeat(aw - cw),
                    new FormattedText("%.2f", currentProgress)
            );
        });
    }

}
