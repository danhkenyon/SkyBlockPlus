package uk.ac.bsfc.sbp.utils.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.SBReflectionUtils;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static final Map<Class<?>, Object> configs = new HashMap<>();

    public static <T> T getConfig(Class<T> type) {
        return type.cast(configs.get(type));
    }

    public static Map<Class<?>, Object> getLoadedConfigs() {
        return configs;
    }

    public static void loadConfigs(String basePackage) {
        List<Class<?>> found = SBReflectionUtils.find(basePackage, ReloadableConfig.class);
        for (Class<?> clazz : found) {
            if (clazz.isAnnotationPresent(ConfigFile.class)) {
                loadConfig(clazz);
            }
        }
    }

    public static void reloadConfig(Class<?> clazz) {
        loadConfig(clazz);
    }

    public static void reloadAll() {
        for (Class<?> clazz : configs.keySet()) {
            reloadConfig(clazz);
        }
    }

    private static void loadConfig(Class<?> clazz) {
        try {
            ConfigFile annotation = clazz.getAnnotation(ConfigFile.class);
            String name = annotation.value().isEmpty()
                    ? clazz.getSimpleName().toLowerCase()
                    : annotation.value();

            Path file = Main.getInstance().getDataFolder().toPath().resolve(name + ".conf");
            Files.createDirectories(file.getParent());

            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (Files.exists(file)) {
                Config cfg = ConfigFactory.parseFile(file.toFile()).resolve();
                applyConfigToObject(instance, cfg);
            }

            String rendered = renderDefaults(instance, 0);
            Files.writeString(file, rendered);

            configs.put(clazz, instance);

        } catch (Exception e) {
            SBLogger.err("<red>Failed to load config for " + clazz.getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void applyConfigToObject(Object target, Config cfg) throws Exception {
        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String key = field.getName();
            Class<?> type = field.getType();

            if (cfg.hasPath(key)) {
                if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class || type == char.class) {
                    field.set(target, castValue(cfg.getAnyRef(key), type));
                } else if (type.isEnum()) {
                    String str = cfg.getString(key);
                    field.set(target, Enum.valueOf((Class<Enum>) type, str));
                } else {
                    Object nestedInstance = field.get(target);
                    if (nestedInstance == null) {
                        nestedInstance = type.getDeclaredConstructor().newInstance();
                        field.set(target, nestedInstance);
                    }
                    applyConfigToObject(nestedInstance, cfg.getConfig(key));
                }
            }
        }
    }

    private static Object castValue(Object value, Class<?> type) {
        if (type == int.class || type == Integer.class) return ((Number) value).intValue();
        if (type == long.class || type == Long.class) return ((Number) value).longValue();
        if (type == double.class || type == Double.class) return ((Number) value).doubleValue();
        if (type == float.class || type == Float.class) return ((Number) value).floatValue();
        if (type == boolean.class || type == Boolean.class) return value;
        if (type == char.class || type == Character.class) {
            String s = value.toString();
            return s.isEmpty() ? '\0' : s.charAt(0);
        }
        if (type == String.class) return value.toString();
        return value;
    }

    private static String renderDefaults(Object obj, int indentLevel) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        String indent = "  ".repeat(indentLevel);

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) continue;

            Comment comment = field.getAnnotation(Comment.class);
            if (comment != null) {
                sb.append(indent).append("# ").append(comment.value()).append("\n");
            }

            Class<?> type = field.getType();
            String key = field.getName();

            if (type.isPrimitive() || Number.class.isAssignableFrom(type) || type == Boolean.class) {
                sb.append(indent).append(key).append(" = ").append(value).append("\n");
            } else if (type == char.class || type == Character.class) {
                sb.append(indent).append(key).append(" = \"").append(value).append("\"\n");
            } else if (type == String.class) {
                sb.append(indent).append(key).append(" = \"").append(value).append("\"\n");
            } else if (type.isEnum()) {
                sb.append(indent).append(key).append(" = \"").append(value.toString()).append("\"\n");
            } else {
                sb.append(indent).append(key).append(" {\n");
                sb.append(renderDefaults(value, indentLevel + 1));
                sb.append(indent).append("}\n");
            }
        }

        return sb.toString();
    }
}
