/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.tool;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class PencilTickCounter {
    public Interaction interaction;
    public int count;
    public MapCanvasRegistry canvas;

    public PencilTickCounter(Interaction i, MapCanvasRegistry canvas, int count) {
        this.interaction = i;
        this.canvas = canvas;
        this.count = count;
    }
}

