package uk.ac.bsfc.sbp.utils.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
            SBLogger.err("&cException occurred while registering commands!\n" + e.getMessage());
        }
    }

    public void register(SBCommand command) {
        if (command.name == null) {
            SBLogger.err("&cYou did not specify a command name!");
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
                SBUser user = SBUser.from(sender);

                if (command.permission != null && !command.permission.isEmpty() && !sender.hasPermission(command.permission)) {
                    user.sendMessage("&cNo Permission!");
                    return true;
                }

                try {
                    command.args(args);
                    command.user = user;

                    command.execute();
                } catch (Exception e) {
                    user.sendMessage("&cAn internal error occurred while executing the command.");
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
                SBUser user = SBUser.from(sender);
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
}
