/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.script;

public class Region {
    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public Region(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean inside(int x, int y) {
        return this.inside(this.x1, this.x2, x) && this.inside(this.y1, this.y2, y);
    }

    private boolean inside(int a, int b, int c) {
        return Math.min(a, b) <= c && Math.max(a, b) >= c;
    }
}

