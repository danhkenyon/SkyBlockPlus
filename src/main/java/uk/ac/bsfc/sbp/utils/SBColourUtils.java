package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SBColourUtils {

    //TODO: Test
    public static Component format(String string){
        return MiniMessage.miniMessage().deserialize(string);
    }


}