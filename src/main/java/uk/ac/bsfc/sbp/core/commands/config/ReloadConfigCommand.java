package uk.ac.bsfc.sbp.core.commands.config;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.config.ConfigManager;
import uk.ac.bsfc.sbp.utils.config.ConfigFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReloadConfigCommand extends SBCommand {

    public ReloadConfigCommand() {
        super();

        this.name("reload-config");
        this.description("Reloads a specified config or all configs.");
        this.permission("sbp.config.reload-config");
    }

    @Override
    public void execute() {
        if (args().length == 0) {
            user.sendMessage("<red>Usage: /reload-config <name|all>");
            return;
        }

        String target = args()[0].toLowerCase();

        if (target.equals("all")) {
            ConfigManager.reloadAll();
            user.sendMessage("<green>All configs reloaded.");
            return;
        }

        Class<?> clazz = getClassByConfigName(target);
        if (clazz == null) {
            user.sendMessage("<red>Unknown config: <white>" + target);
            return;
        }

        ConfigManager.reloadConfig(clazz);
        user.sendMessage("<green>Reloaded config: <white>" + target);
    }

    @Override
    public List<String> suggestions(int index) {
        if (index == 0) {
            List<String> list = new ArrayList<>();
            list.add("all");

            for (Map.Entry<Class<?>, Object> entry : ConfigManager.getLoadedConfigs().entrySet()) {
                Class<?> clazz = entry.getKey();
                String name = getConfigName(clazz);
                list.add(name);
            }

            return list;
        }
        return List.of();
    }

    private String getConfigName(Class<?> clazz) {
        ConfigFile anno = clazz.getAnnotation(ConfigFile.class);

        if (anno != null && !anno.value().isEmpty()) {
            return anno.value().toLowerCase();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    private Class<?> getClassByConfigName(String name) {
        for (Map.Entry<Class<?>, Object> entry : ConfigManager.getLoadedConfigs().entrySet()) {
            Class<?> clazz = entry.getKey();
            String cfgName = getConfigName(clazz);
            if (cfgName.equalsIgnoreCase(name)) return clazz;
        }
        return null;
    }
}
