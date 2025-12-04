package uk.ac.bsfc.sbp.utils.math;

public class SBVector {
    private final double x;
    private final double y;
    private final double z;

    protected SBVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SBVector of(double x, double y, double z) {
        return new SBVector(x, y, z);
    }

    public double getX() {
        return x;
    }
    public double x() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double y() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double z() {
        return z;
    }

    public SBVector add(SBVector other) {
        return new SBVector(
                this.x + other.x,
                this.y + other.y,
                this.z + other.z
        );
    }
    public SBVector sub(SBVector other) {
        return new SBVector(
                this.x - other.x,
                this.y - other.y,
                this.z - other.z
        );
    }
    public SBVector translate(double dx, double dy, double dz) {
        return new SBVector(
                this.x + dx,
                this.y + dy,
                this.z + dz
        );
    }

    @Override
    public String toString() {
        return "SBVector[" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ']';
    }
}
