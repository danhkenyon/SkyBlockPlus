package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SBColourUtils {
    public static Component format(String string){
        return MiniMessage.miniMessage().deserialize(string);
    }
    public static String format(Component string){
        return MiniMessage.miniMessage().serialize(string);
    }
}