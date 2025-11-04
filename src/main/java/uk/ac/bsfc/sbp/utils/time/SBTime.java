package uk.ac.bsfc.sbp.utils.time;

public final class SBTime {
    private final long millis;

    private SBTime() {
        this.millis = System.currentTimeMillis();
    }
    private SBTime(long millis) {
        this.millis = millis;
    }

    public static SBTime now() {
        return new SBTime();
    }
    public long millis() {
        return millis;
    }

    public static String format(SBTimeFormat format) {
        return format.format(SBTime.now().millis());
    }
    public static String format(String format) {
        return SBTime.format(SBTimeFormat.of(format));
    }
}