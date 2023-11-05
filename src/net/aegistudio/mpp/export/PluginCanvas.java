/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.export;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import net.aegistudio.mpp.Interaction;

public interface PluginCanvas
extends Cloneable {
    public void paint(Interaction var1, Color var2);

    public boolean interact(Interaction var1);

    public void load(InputStream var1);

    public void save(OutputStream var1);

    public void add(PluginCanvasRegistry<? extends PluginCanvas> var1);

    public void remove(PluginCanvasRegistry<? extends PluginCanvas> var1);

    public void tick();
}

