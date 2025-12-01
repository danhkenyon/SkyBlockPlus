package uk.ac.bsfc.sbp.utils.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ConfigNodeBuilder {

    private ConfigNodeBuilder() {}

    public static ConfigNode.ObjectNode buildRoot(Object configInstance) {
        try {
            List<ConfigNode> children = buildChildren(configInstance);
            return new ConfigNode.ObjectNode(configInstance.getClass().getSimpleName(), configInstance.getClass(), configInstance, children);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build config node tree", e);
        }
    }

    private static List<ConfigNode> buildChildren(Object instance) throws IllegalAccessException {
        List<ConfigNode> nodes = new ArrayList<>();
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            int mods = field.getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isFinal(mods) || field.isSynthetic() || Modifier.isTransient(mods)) continue;
            field.setAccessible(true);
            Object raw = field.get(instance);
            Class<?> type = field.getType();
            String name = field.getName();

            if (type == boolean.class || type == Boolean.class) {
                Boolean val = raw == null ? Boolean.FALSE : (Boolean) raw;
                nodes.add(new ConfigNode.BooleanNode(name, type, val));
            } else if (type == int.class || type == Integer.class) {
                Integer val = raw == null ? 0 : ((Number) raw).intValue();
                nodes.add(new ConfigNode.IntNode(name, type, val));
            } else if (type == double.class || type == Double.class) {
                Double val = raw == null ? 0.0 : ((Number) raw).doubleValue();
                nodes.add(new ConfigNode.DoubleNode(name, type, val));
            } else if (type == String.class) {
                String val = raw == null ? "" : raw.toString();
                nodes.add(new ConfigNode.StringNode(name, type, val));
            } else if (Collection.class.isAssignableFrom(type)) {
                List<String> values = new ArrayList<>();
                if (raw instanceof Collection<?> col) {
                    for (Object o : col) values.add(o == null ? "" : o.toString());
                }
                nodes.add(new ConfigNode.ListNode(name, type, values));
            } else if (type.isArray() && type.getComponentType() == String.class) {
                List<String> values = new ArrayList<>();
                if (raw != null) {
                    int len = Array.getLength(raw);
                    for (int i = 0; i < len; i++) {
                        Object o = Array.get(raw, i);
                        values.add(o == null ? "" : o.toString());
                    }
                }
                nodes.add(new ConfigNode.ListNode(name, type, values));
            } else if (type.isEnum()) {
                String val = raw == null ? "" : raw.toString();
                nodes.add(new ConfigNode.StringNode(name, type, val));
            } else if (type.getName().startsWith("java.")) {
                String val = raw == null ? "" : raw.toString();
                nodes.add(new ConfigNode.StringNode(name, type, val));
            } else {
                Object nested = raw;
                if (nested == null) {
                    try {
                        nested = type.getDeclaredConstructor().newInstance();
                        field.set(instance, nested);
                    } catch (Exception ignore) {
                        nested = null;
                    }
                }
                if (nested != null) {
                    List<ConfigNode> sub = buildChildren(nested);
                    nodes.add(new ConfigNode.ObjectNode(name, type, nested, sub));
                }
            }
        }
        return nodes;
    }
}