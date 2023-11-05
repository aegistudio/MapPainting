/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp.tool;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Razor
extends Pencil {
    public Razor() {
        this.tapMessage = "@memento.razorTap";
        this.lineMessage = "@memento.razorLine";
    }

    @Override
    public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
        if (itemStack.getType() == Material.SHEARS) {
            super.pencilPaint(interact, canvas, null);
            return true;
        }
        return false;
    }
}

