package uk.ac.bsfc.sbp.utils.strings;

/**
 * The Placeholder class represents a simple key-value pair, where the key is a string
 * and the value can be any object. It is typically used for representing dynamic placeholders
 * that can be substituted with specific values at runtime.
 *
 * This class is immutable and provides a factory method for creating instances.
 *
 * Key features:
 * - Holds two values: a string key (`val`) and an associated object (`obj`).
 * - Designed for use in situations where placeholder-based substitution is required,
 *   such as in dynamic string processing or configuration handling.
 *
 * Methods:
 * - `of`: A static factory method to create a new Placeholder instance with the specified key and value.
 * - `val`: Retrieves the key (string) of the Placeholder.
 * - `obj`: Retrieves the value (object) of the Placeholder.
 */
public class Placeholder {
    private final String val;
    private final Object obj;

    private Placeholder(String val, Object obj) {
        this.val = val;
        this.obj = obj;
    }

    public static Placeholder of(String val, Object obj) {
        return new Placeholder(val, obj);
    }

    public String val() {
        return val;
    }
    public Object obj() {
        return obj;
    }
}
