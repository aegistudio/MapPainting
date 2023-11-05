/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp.tool;

import java.awt.Color;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PaintBucket
implements PaintTool {
    public MapPainting painting;
    public static final String FILL_MESSAGE = "fillMessage";
    public String fillMessage = "@memento.fill";

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        this.painting = painting;
        painting.getLocale(FILL_MESSAGE, this.fillMessage, section);
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }

    @Override
    public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
        Color color = this.painting.m_paintManager.paintBucketRecipe.getColor(itemStack);
        if (color == null) {
            return false;
        }
        canvas.history.add(new PaintFillMemento(this.painting, canvas.canvas, interact, color, this.fillMessage));
        return true;
    }
}

