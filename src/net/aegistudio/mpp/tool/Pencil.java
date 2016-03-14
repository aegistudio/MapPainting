package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class Pencil implements PaintTool {
	public HashMap<MapCanvasRegistry, PencilTickCounter> lastStroke 
		= new HashMap<MapCanvasRegistry, PencilTickCounter>();
	public MapPainting painting;
	
	long interval = 1;
	int initCount = 7;
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		
		painting.getServer().getScheduler().scheduleSyncRepeatingTask(painting, new Runnable() {
			@Override
			public void run() {
				Iterator<Entry<MapCanvasRegistry, PencilTickCounter>> 
					counter = lastStroke.entrySet().iterator();
				while(counter.hasNext()) {
					Entry<MapCanvasRegistry, PencilTickCounter> current = counter.next();
					current.getValue().count --;
					if(current.getValue().count <= 0)
						counter.remove();
				}
			}
		}, interval, interval);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, int x, int y) {
		if(itemStack.getType() == Material.INK_SACK) {
			Color color = painting.palette.dye.getColor(itemStack);
			canvas.canvas.paint(x, y, color);
			PencilTickCounter last = lastStroke.get(canvas);
			if(last != null) {
				double dy = y - last.y;
				double dx = x - last.x;
				
				// Using dda.
				if(dx != 0 || dy != 0) 
					if(Math.abs(dy) >= Math.abs(dx)) {
						int beginX, beginY;
						
						if(dy <= 0) {
							beginX = x;
							beginY = y;
						}
						else {
							beginX = last.x;
							beginY = last.y;
						}
						
						double diff = dx / dy;
						for(int i = 0; i < Math.abs(dy); i ++) 
							canvas.canvas.paint((int) Math.round(beginX + 
									diff * i), beginY + i, color);
					}
					else {
						int beginX, beginY;
						
						if(dx <= 0) {
							beginX = x;
							beginY = y;
						}
						else {
							beginX = last.x;
							beginY = last.y;
						}
						
						double diff = dy / dx;
						for(int i = 0; i < Math.abs(dx); i ++) 
							canvas.canvas.paint(beginX + i, 
									(int) Math.round(beginY + diff * i),  color);
					}
			}
			this.lastStroke.put(canvas, new PencilTickCounter(x, y, initCount));
			
			return true;
		}
		return false;
	}
}
