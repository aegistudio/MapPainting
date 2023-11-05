/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.canvas;

import java.awt.Color;

public class CachedCanvasColor
extends CanvasColor {
    private int[] color;
    private int[] mapped;

    public CachedCanvasColor(int count) {
        this.color = new int[count];
        this.mapped = new int[count];
        for (int i = 0; i < count; ++i) {
            this.color[i] = 0;
            this.mapped[i] = -1;
        }
    }

    @Override
    public int getIndex(Color color) {
        if (color == null) {
            return 0;
        }
        int rgbValue = color.getRGB();
        int hashed = Math.abs(rgbValue % this.color.length);
        if (this.color[hashed] == rgbValue) {
            if (this.mapped[hashed] == -1) {
                this.mapped[hashed] = super.getIndex(color);
            }
            return this.mapped[hashed];
        }
        this.color[hashed] = rgbValue;
        this.mapped[hashed] = super.getIndex(color);
        return this.mapped[hashed];
    }
}

