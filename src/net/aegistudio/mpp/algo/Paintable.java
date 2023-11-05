/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.algo;

import java.awt.Color;

public interface Paintable {
    public void bcolor(byte var1);

    public void color(Color var1);

    public int width();

    public int height();

    public void set(int var1, int var2);

    public Color get(int var1, int var2);

    public byte bget(int var1, int var2);
}

