/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp;

import java.util.Map;
import java.util.TreeMap;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PaintToolManager
implements PaintTool {
    public final TreeMap<String, PaintTool> toolMap = new TreeMap();

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        for (Map.Entry<String, PaintTool> toolEntry : this.toolMap.entrySet()) {
            if (!section.contains(toolEntry.getKey())) {
                section.createSection(toolEntry.getKey());
            }
            ConfigurationSection toolConfig = section.getConfigurationSection(toolEntry.getKey());
            toolEntry.getValue().load(painting, toolConfig);
        }
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
        for (Map.Entry<String, PaintTool> toolEntry : this.toolMap.entrySet()) {
            if (!section.contains(toolEntry.getKey())) {
                section.createSection(toolEntry.getKey());
            }
            ConfigurationSection toolConfig = section.getConfigurationSection(toolEntry.getKey());
            toolEntry.getValue().save(painting, toolConfig);
        }
    }

    @Override
    public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
        for (PaintTool tool : this.toolMap.values()) {
            if (!tool.paint(itemStack, canvas, interact)) continue;
            return true;
        }
        return false;
    }
}

