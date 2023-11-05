package net.aegistudio.mpp.algo;

import java.awt.Color;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.CanvasColor;

public class CanvasAdapter
implements Paintable {
    public final Interaction interact;
    public final Canvas canvas;
    public final CanvasColor canvasColor;
    public Color color;

    public CanvasAdapter(CanvasColor canvasColor, Interaction interact, Color c, Canvas canvas) {
        this.interact = interact;
        this.canvas = canvas;
        this.color = c;
        this.canvasColor = canvasColor;
    }

    @Override
    public void set(int x, int y) {
        this.canvas.paint(this.interact.reCoordinate(x, y), this.color);
    }

    @Override
    public Color get(int x, int y) {
        return this.canvas.look(x, y);
    }

    @Override
    public void color(Color c) {
        this.color = c;
    }

    @Override
    public void bcolor(byte c) {
        this.color = this.canvasColor.getColor(c);
    }

    @Override
    public byte bget(int x, int y) {
        return (byte)this.canvasColor.getIndex(this.get(x, y));
    }

    @Override
    public int width() {
        return this.canvas.size();
    }

    @Override
    public int height() {
        return this.canvas.size();
    }
}

