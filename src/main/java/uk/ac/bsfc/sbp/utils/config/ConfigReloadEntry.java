package uk.ac.bsfc.sbp.utils.config;

import java.lang.reflect.Method;

public class ConfigReloadEntry {
    public final String name;
    public final String fileName;
    public final ReloadableConfig instance;
    public final Method reloadMethod;

    public ConfigReloadEntry(String name, String fileName,
                             ReloadableConfig instance, Method reloadMethod) {
        this.name = name;
        this.fileName = fileName;
        this.instance = instance;
        this.reloadMethod = reloadMethod;
    }
}
