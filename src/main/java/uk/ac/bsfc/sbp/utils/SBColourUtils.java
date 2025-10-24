package uk.ac.bsfc.sbp.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBColourUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern MC_PATTERN = Pattern.compile("&([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);

    private static final String RESET = "\u001B[0m";

    private static final Map<Character, String> ANSI_COLORS = new HashMap<>() {{
        put('0', "\u001B[30m");
        put('1', "\u001B[34m");
        put('2', "\u001B[32m");
        put('3', "\u001B[36m");
        put('4', "\u001B[31m");
        put('5', "\u001B[35m");
        put('6', "\u001B[33m");
        put('7', "\u001B[37m");
        put('8', "\u001B[90m");
        put('9', "\u001B[94m");
        put('a', "\u001B[92m");
        put('b', "\u001B[96m");
        put('c', "\u001B[91m");
        put('d', "\u001B[95m");
        put('e', "\u001B[93m");
        put('f', "\u001B[97m");
        put('r', RESET);
    }};

    @SuppressWarnings("deprecation")
    public static String format(String input) {
        if (input == null) {
            return "";
        }
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            ChatColor color = ChatColor.of("#" + hex);
            matcher.appendReplacement(buffer, color.toString());
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
    public static String ansi(String input) {
        if (input == null) return "";

        Matcher hexMatcher = HEX_PATTERN.matcher(input);
        StringBuilder buffer = new StringBuilder();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            hexMatcher.appendReplacement(
                    buffer,
                    String.format("\u001B[38;2;%d;%d;%dm", r, g, b)
            );
        }
        hexMatcher.appendTail(buffer);
        input = buffer.toString();

        Matcher mcMatcher = MC_PATTERN.matcher(input);
        buffer = new StringBuilder();
        while (mcMatcher.find()) {
            char code = Character.toLowerCase(mcMatcher.group(1).charAt(0));
            String ansi = ANSI_COLORS.getOrDefault(code, "");
            mcMatcher.appendReplacement(buffer, ansi);
        }
        mcMatcher.appendTail(buffer);

        return buffer + RESET;
    }
}