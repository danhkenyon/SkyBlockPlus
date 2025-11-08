package uk.ac.bsfc.sbp.utils.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class SBServer {
    public static void broadcast(SBUser user, String ... msg) {
        for (String line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage("<yellow>[<aqua>BROADCAST<yellow>] <yellow>[<aqua>"+user.getName()+"<yellow>]: <white><b>"+line);
            }
            SBLogger.raw("<yellow>[<aqua>BROADCAST<yellow>] <yellow>[<aqua>"+user.getName()+"<yellow>]: <white><b>"+line);
        }
    }
    public static void broadcast(String ... msg) {
        broadcast(SBUser.from("CONSOLE"), msg);
    }
    public static void broadcastRaw(SBUser user, String ... msg) {
        for (String line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage(line);
            }
            SBLogger.raw("<yellow>[<aqua>BROADCAST<yellow>] <yellow>[<aqua>"+user.getName()+"<yellow>]: <white><b>"+line);
        }
    }
    public static void broadcastRaw(String ... msg) {
        broadcastRaw(SBUser.from("CONSOLE"), msg);
    }
}
