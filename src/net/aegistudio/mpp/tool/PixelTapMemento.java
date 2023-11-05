/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.tool;

import java.awt.Color;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.canvas.Canvas;

public class PixelTapMemento
implements Memento {
    private final Canvas canvas;
    private final Color newColor;
    private final String undoMessage;
    private final Interaction interact;
    private Color oldColor;

    public PixelTapMemento(Canvas canvas, Interaction interact, Color c, String undoMessage) {
        this.canvas = canvas;
        this.newColor = c;
        this.undoMessage = undoMessage;
        this.interact = interact;
    }

    @Override
    public void exec() {
        this.oldColor = this.canvas.look(this.interact.x, this.interact.y);
        this.canvas.paint(this.interact, this.newColor);
    }

    @Override
    public void undo() {
        this.canvas.paint(this.interact, this.oldColor);
    }

    public String toString() {
        if (this.undoMessage == null) {
            return null;
        }
        return this.undoMessage.replace("$x", Integer.toString(this.interact.x)).replace("$y", Integer.toString(this.interact.y)).replace("$r", Integer.toString(this.newColor == null ? -1 : this.newColor.getRed())).replace("$g", Integer.toString(this.newColor == null ? -1 : this.newColor.getGreen())).replace("$b", Integer.toString(this.newColor == null ? -1 : this.newColor.getBlue()));
    }
}

