package uk.ac.bsfc.sbp.utils.command;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBColourUtils;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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