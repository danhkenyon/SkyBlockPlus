package uk.ac.bsfc.sbp.utils.math;

/**
 * The SBMath class provides a utility method for mathematical operations,
 * specifically for rounding double values to a specified precision.
 *
 * This class is designed to help format and process numerical data
 * by reducing a double value to a defined number of decimal places.
 * It ensures consistency when working with calculations or displaying
 * numeric values in a user-friendly format.
 *
 * Thread-safety:
 * The method is thread-safe as it operates on local variables and does
 * not rely on shared or mutable state.
 */
public final class SBMath {
    private SBMath() {}

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
