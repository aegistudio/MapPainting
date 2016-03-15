package net.aegistudio.mpp.tool;

import java.awt.Color;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class PaintBucket implements PaintTool {

	public MapPainting painting;
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		painting.getLocale(FILL_MESSAGE, fillMessage, section);
	}

	public static final String FILL_MESSAGE = "fillMessage";
	public String fillMessage = "Fill starting from [$x, $y] with color ($r, $g, $b).";
	
	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {		}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, int x, int y) {
		Color color = painting.palette.paintBucket.getColor(itemStack);
		if(color == null) return false;
		canvas.history.add(new PaintFillMemoto(canvas.canvas, x, y, color, fillMessage));
		return true;
	}
}