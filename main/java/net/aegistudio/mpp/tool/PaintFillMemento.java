/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.tool;

import java.awt.Color;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.algo.CanvasAdapter;
import net.aegistudio.mpp.algo.FillGenerator;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.CanvasColor;

public class PaintFillMemento
implements Memento {
    private final FillGenerator fill;
    private final CanvasColor canvasColor;
    private final Canvas canvas;
    private final Color fillColor;
    private final String fillMessage;
    private final Interaction interact;
    private Color seedColor;

    public PaintFillMemento(MapPainting mapPainting, Canvas canvas, Interaction interact, Color c, String fillMessage) {
        this.fill = mapPainting.m_assetManager.get("fill", FillGenerator.class);
        this.canvasColor = mapPainting.m_canvasManager.color;
        this.canvas = canvas;
        this.fillColor = c;
        this.fillMessage = fillMessage;
        this.interact = interact;
    }

    @Override
    public void exec() {
        this.seedColor = this.canvas.look(this.interact.x, this.interact.y);
        this.fill.fill(new CanvasAdapter(this.canvasColor, this.interact, this.fillColor, this.canvas), this.interact.x, this.interact.y, this.fillColor);
    }

    public String toString() {
        if (this.fillMessage == null) {
            return null;
        }
        return this.fillMessage.replace("$x", Integer.toString(this.interact.x)).replace("$y", Integer.toString(this.interact.y)).replace("$r", Integer.toString(this.fillColor.getRed())).replace("$g", Integer.toString(this.fillColor.getGreen())).replace("$b", Integer.toString(this.fillColor.getBlue()));
    }

    @Override
    public void undo() {
        this.fill.fill(new CanvasAdapter(this.canvasColor, this.interact, this.seedColor, this.canvas), this.interact.x, this.interact.y, this.seedColor);
    }
}

