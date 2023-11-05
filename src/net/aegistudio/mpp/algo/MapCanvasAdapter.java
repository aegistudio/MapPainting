/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapCanvas
 */
package net.aegistudio.mpp.algo;

import java.awt.Color;
import net.aegistudio.mpp.canvas.CanvasColor;
import org.bukkit.map.MapCanvas;

public class MapCanvasAdapter
implements Paintable {
    public CanvasColor canvasColor;
    public MapCanvas canvas;
    byte color = 0;

    public MapCanvasAdapter(CanvasColor canvasColor, MapCanvas canvas) {
        this.canvasColor = canvasColor;
        this.canvas = canvas;
    }

    @Override
    public void bcolor(byte c) {
        this.color = c;
    }

    @Override
    public void color(Color c) {
        this.color = (byte)this.canvasColor.getIndex(c);
    }

    @Override
    public void set(int x, int y) {
        this.canvas.setPixel(x, 127 - y, this.color);
    }

    @Override
    public Color get(int x, int y) {
        return this.canvasColor.getColor(this.bget(x, y));
    }

    @Override
    public byte bget(int x, int y) {
        return this.canvas.getPixel(x, 127 - y);
    }

    @Override
    public int width() {
        return 128;
    }

    @Override
    public int height() {
        return 128;
    }
}

