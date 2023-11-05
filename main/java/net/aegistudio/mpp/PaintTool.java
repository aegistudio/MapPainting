/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp;

import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.inventory.ItemStack;

public interface PaintTool
extends Module {
    public boolean paint(ItemStack var1, MapCanvasRegistry var2, Interaction var3);
}

