package uk.ac.bsfc.sbp.utils.command;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The SBCommand class provides a base structure for defining and managing commands within an application.
 * It is an abstract class intended to be extended by specific command implementations, offering customizable
 * functionality for various use cases.
 *
 * Features of the class include:
 * - Command name, description, usage instructions, and permission requirements.
 * - Aliases and arguments to enhance flexibility and user experience.
 * - A method to check whether the command is enabled using persistent data storage.
 * - Abstract methods to be implemented for executing the command and providing tab completion suggestions.
 *
 * The class is designed to be used in applications requiring a command framework where commands can
 * be modularly defined and managed. Subclasses are expected to implement the execution and
 * tab-completion logic.
 *
 * Key Methods:
 * - `name`: Getter and setter for the name of the command.
 * - `description`: Getter and setter for a brief description of the command's functionality.
 * - `usage`: Getter and setter for usage instructions.
 * - `permission`: Getter and setter for the required permission to execute the command.
 * - `args`: Getter and setter for the arguments provided to the command.
 * - `aliases`: Getter and setter for the aliases of the command.
 * - `isEnabled`: Checks whether the command is enabled, leveraging persistent data storage for state management.
 * - `execute`: Abstract method meant to be overridden for defining the command's execution logic.
 * - `tabComplete`: Provides tab-completion suggestions based on partially typed arguments.
 * - `suggestions`: Returns a list of potential suggestions for a specific argument index.
 *
 * Subclasses are expected to provide behavior tailored to their specific context, overriding abstract and relevant methods.
 */
public abstract class SBCommand {
    protected String name;
    protected @Nullable String description;
    protected @Nullable String usage;
    protected @Nullable String permission;
    protected String[] args;
    protected String[] aliases;
    protected SBUser user;

    public @NotNull String name() {
        return name;
    }
    public @Nullable String description() {
        return description;
    }
    public @Nullable String usage() {
        return usage;
    }
    public @Nullable String permission() {
        return permission;
    }
    public String[] args() {
        return args;
    }
    public String[] aliases() {
        return aliases;
    }
    public Boolean isEnabled(){
        PersistentDataContainer pdc = Main.getInstance().getGlobalContainer();
        if (!pdc.has(NKeys.getKey(name))){
            pdc.set(NKeys.getKey(name), PersistentDataType.BOOLEAN, true);
        }
        return pdc.get(NKeys.getKey(name), PersistentDataType.BOOLEAN);
    }

    public void name(@NotNull String name) {
        this.name = name;
    }
    public void description(@Nullable String description) {
        this.description = description;
    }
    public void usage(@Nullable String usage) {
        this.usage = usage;
    }
    public void permission(@Nullable String permission) {
        this.permission = permission;
    }
    public void args(String ... args) {
        this.args = args;
    }
    public void aliases(String ... aliases) {
        this.aliases = aliases;
    }

    public SBUser getUser() {
        return user;
    }

    protected SBCommand() {
    }

    public void execute() {}
    public List<String> tabComplete() {
        String[] array = args;
        if (args == null || array.length == 0) {
            return List.of();
        }
        int index = array.length - 1;
        String prefix = array[index].toLowerCase();

        List<String> suggestions = this.suggestions(index);
        if (suggestions.isEmpty()) {
            return List.of();
        }

        List<String> results = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(prefix)) {
                results.add(suggestion);
            }
        }
        return results;
    }
    public List<String> suggestions(int index) {
        return List.of();
    }
}