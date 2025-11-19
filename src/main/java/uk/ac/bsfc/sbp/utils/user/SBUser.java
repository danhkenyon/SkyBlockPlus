package uk.ac.bsfc.sbp.utils.user;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBColourUtils;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.tables.UserTable;
import uk.ac.bsfc.sbp.utils.strings.Messages;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Represents an abstract user in the system. This class provides a foundation
 * for handling different types of users, such as console users and player users,
 * and provides various utility methods for interacting with and managing users.
 * <p>
 * Subclasses that extend this class must define specific behaviors for certain
 * types of users.
 */
public abstract class SBUser {
    protected final Class<? extends SBUser> clazz = this.getClass();
    protected final SBUserType userType = SBUserType.fromClass(clazz.getSimpleName());

    protected final UUID uuid;
    protected final String name;
    protected long flight_time;
    protected UUID island_id;
    protected String island_rank;
    protected final boolean console;

    protected List<Placeholder> userPlaceholders;

    protected SBUser(String name, UUID uuid, long flight_time, UUID island_id, String island_rank, boolean console) {
        this.name = name;
        this.uuid = uuid;

        this.flight_time = flight_time;
        this.island_id = island_id;
        this.island_rank = island_rank;

        this.console = console;

        userPlaceholders = List.of(
                Placeholder.of("%user.name%", this.getName()),
                Placeholder.of("%user.uuid%", this.getUniqueID().toString()),
                Placeholder.of("%user.console%", this.isConsole())
        );
    }

    // ---------- FACTORY ---------- //

    public static @NotNull SBUser console() {
        return new SBConsole();
    }

    public static @NotNull SBUser from(UUID uuid) {
        if (uuid == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return new SBConsole();
        }

        SBUser user = UserTable.getInstance().getRow("uuid", uuid);
        if (user == null) {
            SBLogger.err("<red>Could not find user! UUID["+uuid+"]");
            throw new NullPointerException();
        }
        return user;
    }
    public static @NotNull SBUser from(String name) {
        if (name.equalsIgnoreCase("CONSOLE")) {
            return new SBConsole();
        }

        SBUser user = UserTable.getInstance().getRow("name", name);
        if (user == null) {
            SBLogger.err("<red>Could not find user! Name["+name+"]");
            throw new NullPointerException();
        }
        return user;
    }

    public static @NotNull SBUser create(UUID uuid, String username, long flight_time, UUID island_id, String island_rank) {
        if (uuid == UUID.fromString("00000000-0000-0000-0000-000000000000")) {
            return new SBConsole();
        }

        return new SBPlayer(username, uuid, flight_time, island_id, island_rank);
    }

    public boolean isConsole() {
        return console;
    }
    public UUID getUniqueID() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public long getFlightTime() {
        return flight_time;
    }
    public UUID getIslandId() {
        return island_id;
    }
    public String getIslandRank() {
        return island_rank;
    }
    public SBUserType getUserType() {
        return userType;
    }

    public void setFlightTime(long flight_time) {
        this.flight_time = flight_time;
    }
    public void setIslandId(UUID island_id) {
        this.island_id = island_id;
    }
    public void setIslandRank(String island_rank) {
        this.island_rank = island_rank;
    }

    public <T extends SBUser> T to(Class<T> clazz) {
        if (clazz.isInstance(this)) return clazz.cast(this);
        if (clazz == SBConsole.class && this.isConsole()) return clazz.cast(new SBConsole());
        if (clazz == SBPlayer.class && !this.isConsole()) return clazz.cast(new SBPlayer(this.name, this.uuid, this.flight_time, this.island_id, this.island_rank));
        if (clazz == SBUser.class) return clazz.cast(this);

        SBLogger.err("<red>Cannot convert " + this.getClass().getSimpleName() + " to " + clazz.getSimpleName());
        return null;
    }
    public <T extends CommandSender> T toBukkit(Class<T> clazz) {
        if (clazz.isInstance(Bukkit.getConsoleSender()) && this.isConsole()) {
            return clazz.cast(Bukkit.getConsoleSender());
        }
        if (Player.class.isAssignableFrom(clazz) && !this.isConsole()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                return clazz.cast(player);
            }
            return null;
        }

        SBLogger.err("<red>Cannot convert " + this.getClass().getSimpleName() + " to " + clazz.getSimpleName() + " (unsupported type)");
        return null;
    }

    public void sendMessage(String message, Placeholder ... placeholders) {
        this.msg(message, Stream.concat(Arrays.stream(userPlaceholders.toArray(Placeholder[]::new)), Arrays.stream(placeholders)).toArray(Placeholder[]::new));
    }
    public void sendMessage(String message) {
        this.msg(message, userPlaceholders.toArray(Placeholder[]::new));
    }
    public void sendMessage(String ... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }
    public void sendMessage(Component message, Placeholder ... placeholders) {
        this.msg(message, Stream
                .concat(Arrays.stream(userPlaceholders.toArray(Placeholder[]::new)), Arrays.stream(placeholders))
                .toArray(Placeholder[]::new));
    }
    public void sendMessage(Component message) {
        this.msg(message, userPlaceholders.toArray(Placeholder[]::new));
    }
    public void sendMessage(Component ... messages) {
        for (Component message : messages) {
            this.sendMessage(message);
        }
    }
    private void msg(String message, Placeholder[] userPlaceholders) {
        Component formatted = SBColourUtils.format(Messages.get(message, userPlaceholders));
        if (this.isConsole()) {
            Bukkit.getConsoleSender().sendMessage(formatted);
        } else {
            Player player = this.toBukkit(Player.class);
            if (player != null && player.isOnline()) {
                player.sendMessage(formatted);
            }
        }
    }
    private void msg(Component message, Placeholder[] userPlaceholders) {
        String parsed = Messages.get(SBColourUtils.format(message), userPlaceholders);

        Component formatted = SBColourUtils.format(parsed);
        if (this.isConsole()) {
            Bukkit.getConsoleSender().sendMessage(formatted);
        } else {
            Player player = this.toBukkit(Player.class);
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
            SBLogger.err("<red>[SUDO] Cannot execute empty command (<white>"+user.getName()+"->"+this.getName()+"<red>)!");
            return;
        }
        if (amt < 1 || amt > 10000) {
            SBLogger.err("<red>[SUDO] Invalid sudo command amount (<white>"+user.getName()+"->"+this.getName()+"<red>)!");
            return;
        }
        if (this == user) {
            SBLogger.err("<red>[SUDO] Attempt to sudo self. (<white>"+user.getName()+"<red>)!");
            return;
        }

        content = content.toLowerCase();
        final SBSudoType type = content.startsWith("c://")
                ? SBSudoType.CHAT
                : SBSudoType.COMMAND;

        switch (type) {
            case CHAT -> this.forceChat(user, content.substring(4), amt);
            case COMMAND -> this.forceCommand(user, content, amt);
            default -> SBLogger.err("<red>[SUDO] Unknown sudo type!");
        }
    }
    private void forceChat(@NotNull SBUser user, String chat, int amt) {
        SBLogger.raw("<white>[SUDO] (<green>"+user.getName()+"<white>) <yellow>Forcing <white>"+this.getName()+" <yellow>to say \"<white>"+chat+"<yellow>\" <white>(<yellow>"+amt+"x<white>)");
        for (int i = 0; i < amt; i++) {
            if (this.getUserType() == SBUserType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say " + SBColourUtils.format("[CONSOLE] " + chat));
                continue;
            }
            this.toBukkit(Player.class).chat(chat);
        }
    }
    private void forceCommand(@NotNull SBUser user, String command, int amt) {
        SBLogger.raw("<white>[SUDO] (<green>"+user.getName()+"<white>) <yellow>Forcing <white>"+this.getName()+" <yellow>to run \"<white>"+command+"<yellow>\" <white>(<yellow>"+amt+"x<white>)");
        for (int i = 0; i < amt; i++) {
            if (this.getUserType() == SBUserType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            } else {
                this.toBukkit(Player.class).performCommand(command);
            }
        }
    }

    @Override
    public String toString() {
        return "SBUser[" +
                "name=" + name +
                ", uuid=" + uuid +
                ", console=" + console +
                ", userType=" + userType +
                ']';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SBUser that)) return false;
        return Objects.equals(uuid, that.uuid);
    }
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public enum SBUserType {
        UNDEFINED,
        PLAYER,
        CONSOLE;

        public static SBUserType fromClass(String name) {
            if (name.equalsIgnoreCase("SBConsole")) {
                return CONSOLE;
            } else if (name.equalsIgnoreCase("SBPlayer")) {
                return PLAYER;
            } else {
                return UNDEFINED;
            }
        }
    }
    public enum SBSudoType {
        CHAT,
        COMMAND;
    }
}