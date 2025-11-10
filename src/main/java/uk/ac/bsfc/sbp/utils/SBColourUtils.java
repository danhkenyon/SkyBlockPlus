package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * The {@code SBColourUtils} class provides utility methods for formatting
 * text to and from MiniMessage components.
 *
 * It is designed for converting between raw text and MiniMessage serialized
 * or deserialized {@code Component} objects, making it easier to work with
 * formatted messages in applications that use the Adventure library.
 *
 * Features:
 * - Serialize Adventure {@code Component} objects into MiniMessage strings.
 * - Deserialize MiniMessage strings into Adventure {@code Component} objects.
 */
public class SBColourUtils {
    public static Component format(String string){
        return MiniMessage.miniMessage().deserialize(string);
    }
    public static String format(Component string){
        return MiniMessage.miniMessage().serialize(string);
    }
}