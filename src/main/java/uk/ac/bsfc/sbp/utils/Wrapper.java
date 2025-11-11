package uk.ac.bsfc.sbp.utils;

/**
 * The Wrapper class serves as an abstract base for creating wrapper objects
 * for translating or converting objects into specific types, such as Bukkit objects.
 *
 * This class is designed to be extended by subclasses that provide domain-specific
 * implementations for the conversion process. It allows for simplifying data conversions
 * by enforcing the implementation of the {@code toBukkit()} method.
 *
 * @param <T> the type of the object to which this wrapper will convert
 */
public abstract class Wrapper<T> {
    public abstract T toBukkit();
}
