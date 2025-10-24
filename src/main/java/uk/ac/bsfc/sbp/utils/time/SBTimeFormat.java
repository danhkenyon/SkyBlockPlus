package uk.ac.bsfc.sbp.utils.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class SBTimeFormat {
    private final SimpleDateFormat formatter;

    private SBTimeFormat(String pattern) {
        this.formatter = new SimpleDateFormat(pattern);
    }

    public static SBTimeFormat of(String pattern) {
        return new SBTimeFormat(pattern);
    }

    public String format(long millis) {
        return formatter.format(new Date(millis));
    }
    public String now() {
        return format(System.currentTimeMillis());
    }
}