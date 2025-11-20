package uk.ac.bsfc.sbp.utils.server;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.List;

public class SBServer {
    public static void broadcast(String ... msg) {
        for (String line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
            }
            SBLogger.raw("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
        }
    }
    public static void broadcastRaw(String ... msg) {
        for (String line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage(line);
            }
            SBLogger.raw("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
        }
    }
    public static void broadcast(Component... msg) {
        for (Component line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
            }
            SBLogger.raw("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
        }
    }
    public static void broadcastRaw(Component ... msg) {
        for (Component line : msg) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SBUser.from(p.getUniqueId()).sendMessage(line);
            }
            SBLogger.raw("<yellow>[<aqua><bold>BROADCAST<yellow>] <white><b>"+line);
        }
    }

    public static List<SBUser> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> SBUser.from(p.getUniqueId()))
                .toList();
    }
    public static List<SBUser> getAllUsers() {
        return null; //UserTable.getInstance().getRows();
    }
}
