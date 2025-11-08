package uk.ac.bsfc.sbp.utils.strings;

import uk.ac.bsfc.sbp.utils.data.SBConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^{}]+)}");

    public static String get(String key, Placeholder... placeholders) {
        key = Messages.applyConfig(key);
        key = Messages.applyRuntime(key, placeholders);

        return key;
    }

    private static String applyConfig(String input) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String placeholderKey = matcher.group(1);
            String replacement = SBConfig.getString(placeholderKey);

            System.out.println(placeholderKey);
            System.out.println(replacement);

            if (replacement.contains("{")) {
                replacement = applyConfig(replacement);
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
    private static String applyRuntime(String input, Placeholder ... placeholders) {
        String result = input;
        for (Placeholder ph : placeholders) {
            result = result.replace(ph.val(), String.valueOf(ph.obj()));
        }
        return result;
    }
}
