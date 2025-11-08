package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBColourUtils {

    //TODO: Test
    public static Component format(String string){
        return MiniMessage.miniMessage().deserialize(string);
    }


}