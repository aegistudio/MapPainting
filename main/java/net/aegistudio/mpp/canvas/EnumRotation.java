/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.canvas;

public enum EnumRotation {
    NONE(1.0, 0.0, 0.0, 1.0),
    CLOCKWISE_45(0.0, -1.0, 1.0, 0.0),
    CLOCKWISE(-1.0, 0.0, 0.0, -1.0),
    CLOCKWISE_135(0.0, 1.0, -1.0, 0.0),
    FLIPPED(1.0, 0.0, 0.0, 1.0),
    FLIPPED_45(0.0, -1.0, 1.0, 0.0),
    COUNTER_CLOCKWISE(-1.0, 0.0, 0.0, -1.0),
    COUNTER_CLOCKWISE_45(0.0, 1.0, -1.0, 0.0);
    
    public final double x1;
    public final double y1;
    public final double x2;
    public final double y2;

    private EnumRotation(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double u(double u, double v) {
        return this.x1 * u + this.y1 * v;
    }

    public double v(double u, double v) {
        return this.x2 * u + this.y2 * v;
    }
}

