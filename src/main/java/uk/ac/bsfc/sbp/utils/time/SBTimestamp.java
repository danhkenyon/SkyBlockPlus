package uk.ac.bsfc.sbp.utils.time;

public final class SBTimestamp {
    private final long millis;

    private SBTimestamp() {
        this.millis = System.currentTimeMillis();
    }
    private SBTimestamp(long millis) {
        this.millis = millis;
    }

    public static SBTimestamp now() {
        return new SBTimestamp();
    }
    public long millis() {
        return millis;
    }

    public String get(SBTimeFormat format) {
        return format.format(millis);
    }
}