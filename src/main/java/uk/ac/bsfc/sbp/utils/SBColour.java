package uk.ac.bsfc.sbp.utils;

public enum SBColour {
    BLACK('0', "\u001B[30m"),
    DARK_BLUE('1', "\u001B[34m"),
    GREEN('2', "\u001B[32m"),
    AQUA('3', "\u001B[36m"),
    RED('4', "\u001B[31m"),
    MAGENTA('5', "\u001B[35m"),
    GOLD('6', "\u001B[33m"),
    GRAY('7', "\u001B[37m"),
    DARK_GRAY('8', "\u001B[90m"),
    BLUE('9', "\u001B[94m"),
    LIME('a', "\u001B[92m"),
    CYAN('b', "\u001B[96m"),
    LIGHT_RED('c', "\u001B[91m"),
    PINK('d', "\u001B[95m"),
    YELLOW('e', "\u001B[93m"),
    WHITE('f', "\u001B[97m"),

    OBFUSCATED('k', ""),
    BOLD('l', ""),
    STRIKETHROUGH('m', ""),
    UNDERLINE('n', ""),
    ITALIC('o', ""),
    RESET('r', "\u001B[0m");

    private final char mc;
    private final String ansi;

    SBColour(char mcCode, String ansiCode) {
        this.mc = mcCode;
        this.ansi = ansiCode;
    }

    public char asMinecraft() {
        return this.mc;
    }
    public String asANSI() {
        return this.ansi;
    }
}
