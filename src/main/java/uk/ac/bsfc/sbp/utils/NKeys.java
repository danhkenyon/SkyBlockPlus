package uk.ac.bsfc.sbp.utils;

import org.bukkit.NamespacedKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The NKeys class provides a utility for managing and retrieving {@link NamespacedKey} instances.
 * It maintains a thread-safe internal cache of keys, ensuring that the same key is not created multiple times.
 */
public final class NKeys {
    private NKeys(){}

    private static final ConcurrentMap<String, NamespacedKey> keys = new ConcurrentHashMap<>();

    public static NamespacedKey getKey(String key) {
        return keys.computeIfAbsent(key, k -> new NamespacedKey("sbp", k));
    }
}
