/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.ArrayList;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.algo.LineGenerator;
import net.aegistudio.mpp.algo.Paintable;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.CanvasColor;

public class LineDrawingMemento
implements Memento,
Paintable {
    private final LineGenerator line;
    private final Canvas canvas;
    private final CanvasColor canvasColor;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final Color lineColor;
    private final String undoMessage;
    private final Interaction interact;
    ArrayList<PixelTapMemento> subMemoto;

    @Override
    public void color(Color c) {
    }

    @Override
    public void bcolor(byte c) {
    }

    public LineDrawingMemento(MapPainting painting, Canvas canvas, int x1, int y1, int x2, int y2, Color c, String undoMessage, Interaction interact) {
        this.line = painting.m_assetManager.get("line", LineGenerator.class);
        this.canvasColor = painting.m_canvasManager.color;
        this.canvas = canvas;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.lineColor = c;
        this.undoMessage = undoMessage;
        this.interact = interact;
    }

    public String toString() {
        if (this.undoMessage == null) {
            return null;
        }
        return this.undoMessage.replace("$x1", Integer.toString(this.x1)).replace("$y1", Integer.toString(this.y1)).replace("$x2", Integer.toString(this.x2)).replace("$y2", Integer.toString(this.y2)).replace("$r", Integer.toString(this.lineColor == null ? -1 : this.lineColor.getRed())).replace("$g", Integer.toString(this.lineColor == null ? -1 : this.lineColor.getGreen())).replace("$b", Integer.toString(this.lineColor == null ? -1 : this.lineColor.getBlue()));
    }

    @Override
    public void exec() {
        if (this.subMemoto == null) {
            this.subMemoto = new ArrayList<PixelTapMemento>();
            this.line.line(this, this.x1, this.y1, this.x2, this.y2);
        }
        for (Memento memoto : this.subMemoto) {
            memoto.exec();
        }
    }

    @Override
    public void undo() {
        if (this.subMemoto != null) {
            for (Memento memoto : this.subMemoto) {
                memoto.undo();
            }
        }
    }

    @Override
    public int width() {
        return this.canvas.size();
    }

    @Override
    public int height() {
        return this.canvas.size();
    }

    @Override
    public void set(int x, int y) {
        this.subMemoto.add(new PixelTapMemento(this.canvas, this.interact.reCoordinate(x, y), this.lineColor, null));
    }

    @Override
    public Color get(int x, int y) {
        return this.canvas.look(x, y);
    }

    @Override
    public byte bget(int x, int y) {
        return (byte)this.canvasColor.getIndex(this.canvas.look(x, y));
    }
}

