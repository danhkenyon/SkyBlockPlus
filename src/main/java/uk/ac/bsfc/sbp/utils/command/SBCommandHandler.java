package uk.ac.bsfc.sbp.utils.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.SBReflectionUtils;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the registration and execution of SBCommand objects within the application.
 *
 * This singleton class acts as a central handler for managing commands, including their registration,
 * execution, and tab-completion. It provides utility methods for retrieving command metadata
 * and ensures commands are registered with the underlying command map used for execution.
 *
 * Features:
 * - Registers SBCommand objects dynamically based on reflection.
 * - Associates commands with metadata such as names, descriptions, usage instructions, permissions,
 *   and aliases.
 * - Routes execution and tab-completion logic to the associated SBCommand instance.
 * - Retrieves all registered commands or their names for further use.
 *
 * Thread Safety:
 * - Use of immutable collections ensures safe read access to the list of commands.
 * - Singleton pattern ensures only one instance of the handler exists throughout the application lifecycle.
 *
 * Usage:
 * - Use the `getInstance()` method to retrieve the singleton instance of this class.
 * - Use the `register()` methods to register commands dynamically or individually.
 * - Use `getCommands()` and `getCommandNames()` to retrieve registered commands' metadata.
 */
public class SBCommandHandler {
    private final List<SBCommand> commands = new ArrayList<>();

    private SBCommandHandler() {}
    private static final SBCommandHandler INSTANCE = new SBCommandHandler();
    public static SBCommandHandler getInstance() {
        return INSTANCE;
    }

    public void register() {
        try {
            List<Class<?>> potential = SBReflectionUtils.find("uk.ac.bsfc.sbp.core", SBCommand.class);
            for (Class<?> clazz : potential) {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    SBCommand cmd = (SBCommand) clazz.getDeclaredConstructor().newInstance();
                    register(cmd);
                }
            }
        } catch (Exception e) {
            SBLogger.err("<red>Exception occurred while registering commands!\n" + e.getMessage());
        }
    }

    public void register(SBCommand command) {
        if (command.name == null) {
            SBLogger.err("<red>You did not specify a command name!");
            throw new IllegalArgumentException();
        }

        Command bukkitCmd = new Command(
                command.name(),
                command.description == null ? "" : command.description,
                command.usage == null ? "/" + command.name : command.usage,
                command.aliases() != null ? Arrays.asList(command.aliases) : List.of()
        ) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
                SBUser user = SBUser.from(sender.getName());

                if (!command.isEnabled() && !user.toBukkit(Player.class).hasPermission("sbp.command-bypass")){
                    user.sendMessage("<red>This command is disabled!");
                    return false;
                }

                if (command.permission != null && !command.permission.isEmpty() && !sender.hasPermission(command.permission)) {
                    user.sendMessage("<red>No Permission!");
                    return true;
                }

                try {
                    command.args(args);
                    command.user = user;

                    command.execute();
                } catch (Exception e) {
                    user.sendMessage("<red>An internal error occurred while executing the command.");
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
                SBUser user = SBUser.from(sender.getName());
                try {
                    command.args(args);
                    command.user = user;

                    List<String> result = command.tabComplete();
                    return result != null ? result : Collections.emptyList();
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
        };

        Bukkit.getCommandMap().register(Main.getInstance().getName().toLowerCase(), bukkitCmd);
        commands.add(command);
    }

    public List<SBCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<String> getCommandNames() {
        List<String> names = new ArrayList<>();
        getCommands().forEach(cmd -> {
            names.add(cmd.name);
        });

        return names;
    }
}
