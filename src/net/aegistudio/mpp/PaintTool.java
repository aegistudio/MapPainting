package net.aegistudio.mpp;

import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.canvas.MapCanvasRegistry;

/**
 * Use responsible chain to handle this plz.
 * @author aegistudio
 */

public interface PaintTool extends Module {
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, InteractInfo interact);
}
