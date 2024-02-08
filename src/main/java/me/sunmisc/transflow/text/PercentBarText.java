package me.sunmisc.transflow.text;

public class PercentBarText implements Text {
    private static final int TOTAL_PROGRESS = 100;
    private static final int BAR_WIDTH = 50;
    private static final String FORMAT
            = "\r[%s] %s%%";

    private final Number value;

    public PercentBarText(Number value) {
        this.value = value;
    }


    @Override
    public String asString() {
        int barWidth = BAR_WIDTH;

        double currentProgress = value.doubleValue();

        int completedWidth =
                (int) (barWidth * (currentProgress / TOTAL_PROGRESS));

        return String.format(FORMAT,
                "=".repeat(completedWidth) +
                " ".repeat(barWidth - completedWidth),
                String.format("%.2f", currentProgress)
        );
    }

    @Override
    public String toString() {
        return asString();
    }
}
