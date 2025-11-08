package uk.ac.bsfc.sbp.utils;

public enum SBColour {
    BLACK('0', "\u001B[30m", "<#000000>"),
    DARK_BLUE('1', "\u001B[34m", "<#0000AA>"),
    DARK_GREEN('2', "\u001B[32m", "<#00AA00>"),
    DARK_AQUA('3', "\u001B[36m", "<#00AAAA>"),
    DARK_RED('4', "\u001B[31m", "<#AA0000>"),
    DARK_PURPLE('5', "\u001B[35m", "<#AA00AA>"),
    GOLD('6', "\u001B[33m", "<#FFAA00>"),
    GRAY('7', "\u001B[37m", "<#AAAAAA>"),
    DARK_GRAY('8', "\u001B[90m", "<#555555>"),
    BLUE('9', "\u001B[94m", "<#5555FF>"),
    GREEN('a', "\u001B[92m", "<#55FF55>"),
    AQUA('b', "\u001B[96m", "<#55FFFF>"),
    RED('c', "\u001B[91m", "<#FF5555>"),
    LIGHT_PURPLE('d', "\u001B[95m", "<#FF55FF>"),
    YELLOW('e', "\u001B[93m", "<#FFFF55>"),
    WHITE('f', "\u001B[97m", "<#FFFFFF>"),

    OBFUSCATED('k', "", "<obf>"),
    BOLD('l', "", "<b>"),
    STRIKETHROUGH('m', "", "<st>"),
    UNDERLINE('n', "", "<u>"),
    ITALIC('o', "", "<i>"),
    RESET('r', "\u001B[0m", "<reset>");

    private final char mc;
    private final String ansi;
    private final String minimessage;

    SBColour(char mcCode, String ansiCode, String minimessage) {
        this.mc = mcCode;
        this.ansi = ansiCode;
        this.minimessage = minimessage;
    }

    public char asMinecraft() {
        return this.mc;
    }
    public String asANSI() {
        return this.ansi;
    }
    public String asMiniMessage(){
        return this.minimessage;
    }
}
