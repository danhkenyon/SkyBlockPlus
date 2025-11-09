package uk.ac.bsfc.sbp.utils;

import org.bukkit.NamespacedKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NKeys {
    private static final ConcurrentMap<String, NamespacedKey> keys = new ConcurrentHashMap<>();

    public static NamespacedKey getKey(String key) {
        return keys.computeIfAbsent(key, k -> new NamespacedKey("sbp", k));
    }
}
