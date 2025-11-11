package uk.ac.bsfc.sbp.utils.time;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SBTimeFormat is a utility class for formatting time values based on a specified pattern.
 *
 * This class leverages Java's SimpleDateFormat to format timestamps in milliseconds or the current system time.
 * Instances of SBTimeFormat are immutable and created using the static factory method.
 */
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