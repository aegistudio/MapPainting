package net.aegistudio.mpp.tool;

import java.awt.Color;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class PaintBucket implements PaintTool {

	public MapPainting painting;
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {		}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, int x, int y) {
		Color color = painting.palette.paintBucket.getColor(itemStack);
		if(color == null) return false;
		this.seedFill(canvas.canvas, x, y, color, 
				canvas.canvas.look(x, y));
		return true;
	}

	private void seedFill(Canvas canvas, int i, int j, Color c, Color seed) {
		if(seed == null) return;
		if(c.getRGB() == seed.getRGB()) return;
		if(!inRange(canvas, i, j)) return;
		
		canvas.paint(i, j, c);
		
		int xmin = i - 1;
		for(; xmin >= 0; xmin --) {
			Color color = canvas.look(xmin, j);
			if(color == null) break;
			if(color.getRGB() != seed.getRGB()) break;
			canvas.paint(xmin, j, c);
		}
		
		int xmax = i + 1;
		for(; xmax <= canvas.size(); xmax ++) {
			Color color = canvas.look(xmax, j);
			if(color == null) break;
			if(color.getRGB() != seed.getRGB()) break;
			canvas.paint(xmax, j, c);
		}
		
		for(int p = xmin + 1; p < xmax; p ++) {
			Color up = canvas.look(p, j + 1);
			if(up != null) {
				if(up.getRGB() == seed.getRGB())
					seedFill(canvas, p, j + 1, c, seed);
			}
			
			Color down = canvas.look(p, j - 1);
			if(down != null) {
				if(down.getRGB() == seed.getRGB()) 
					seedFill(canvas, p, j - 1, c, seed);
			}
		}
	}
	
	private boolean inRange(Canvas c, int i, int j) {
		if(i < 0) return false;
		if(j < 0) return false;
		if(i >= c.size()) return false;
		if(j >= c.size()) return false;
		return true;
	}
}