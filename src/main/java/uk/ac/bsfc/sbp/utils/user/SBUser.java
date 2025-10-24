package uk.ac.bsfc.sbp.utils.user;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBColourUtils;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.UserDatabase;

import java.util.UUID;

public abstract class SBUser {
    protected final String name;
    protected final UUID uuid;
    protected final boolean console;

    protected final Class<? extends SBUser> clazz = this.getClass();
    protected final SBUserType userType = SBUserType.fromClass(clazz.getSimpleName());

    protected SBUser(String name, UUID uuid, boolean console) {
        this.name = name;
        this.uuid = uuid;
        this.console = console;
    }

    // ---------- FACTORY ---------- //

    @Contract("null -> new")
    public static @NotNull SBUser from(CommandSender sender) {
        if (sender instanceof Player p) {
            return new SBPlayer(p.getName(), p.getUniqueId());
        } else {
            return new SBConsole();
        }
    }
    public static @NotNull SBUser from(UUID uuid) {
        if (uuid == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return new SBConsole();
        }

        SBLogger.raw(uuid.toString());
        SBUser user = UserDatabase.fetchUser(uuid);
        if (user == null) {
            SBLogger.err("&cCould not find user! UUID["+uuid+"]");
            throw new NullPointerException();
        }
        return user;
    }
    public static @NotNull SBUser from(String name) {
        if (name.equalsIgnoreCase("CONSOLE")) {
            return new SBConsole();
        }

        SBUser user = UserDatabase.fetchUser(name);
        if (user == null) {
            SBLogger.err("&cCould not find user! Name["+name+"]");
            throw new NullPointerException();
        }
        return user;
    }

    public static @NotNull SBUser from(UUID uuid, String username) {
        if (uuid == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return new SBConsole();
        }

        return new SBPlayer(username, uuid);
    }

    // ---------- CORE ---------- //

    public boolean console() {
        return console;
    }
    public UUID uuid() {
        return uuid;
    }
    public String username() {
        return name;
    }
    public SBUserType userType() {
        return userType;
    }

    public Player toBukkit() {
        if (console()) {
            SBLogger.err("&cAttempted to retrieve a Player from CommandSender!");
            throw new NullPointerException();
        }
        return Bukkit.getPlayer(uuid);
    }

    // ---------- MESSAGING ---------- //

    public void sendMessage(String message) {
        String formatted = SBColourUtils.format(message);
        if (console()) {
            Bukkit.getConsoleSender().sendMessage(formatted);
        } else {
            Player player = toBukkit();
            if (player != null && player.isOnline()) {
                player.sendMessage(formatted);
            }
        }
    }

    public void sudo(SBUser user, String content) {
        this.sudo(user, content, 1);
    }
    public void sudo(SBUser user, String content, int amt) {
        // TODO: Validate user permissions.

        if (content.isEmpty()) {
            SBLogger.err("&c[SUDO] Cannot execute empty command (&f"+user.username()+"->"+this.username()+"&c)!");
            return;
        }
        if (amt < 1 || amt > 10000) {
            SBLogger.err("&c[SUDO] Invalid sudo command amount (&f"+user.username()+"->"+this.username()+"&c)!");
            return;
        }
        if (this == user) {
            SBLogger.err("&c[SUDO] Attempt to sudo self. (&f"+user.username()+"&c)!");
            return;
        }

        content = content.toLowerCase();
        final SBSudoType type = content.startsWith("c://")
                ? SBSudoType.CHAT
                : SBSudoType.COMMAND;

        switch (type) {
            case CHAT -> this.forceChat(user, content.substring(4), amt);
            case COMMAND -> this.forceCommand(user, content, amt);
            default -> SBLogger.err("&c[SUDO] Unknown sudo type!");
        }
    }

    private void forceChat(@NotNull SBUser user, String chat, int amt) {
        SBLogger.raw("&f[SUDO] (&a"+user.username()+"&f) &eForcing &f"+this.username()+" &eto say \"&f"+chat+"&e\" &f(&e"+amt+"x&f)");
        for (int i = 0; i < amt; i++) {
            if (this.userType() == SBUserType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say " + SBColourUtils.format("[CONSOLE] " + chat));
                continue;
            }
            this.toBukkit().chat(SBColourUtils.format(chat));
        }
    }
    private void forceCommand(@NotNull SBUser user, String command, int amt) {
        SBLogger.raw("&f[SUDO] (&a"+user.username()+"&f) &eForcing &f"+this.username()+" &eto run \"&f"+command+"&e\" &f(&e"+amt+"x&f)");
        for (int i = 0; i < amt; i++) {
            if (this.userType() == SBUserType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), SBColourUtils.format(command));
            } else {
                this.toBukkit().performCommand(SBColourUtils.format(command));
            }
        }
    }
}