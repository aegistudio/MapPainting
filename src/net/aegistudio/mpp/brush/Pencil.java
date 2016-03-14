package net.aegistudio.mpp.brush;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.scheduler.BukkitRunnable;

public class Pencil implements PaintTool {
	public HashMap<MapCanvasRegistry, PencilTickCounter> lastStroke 
		= new HashMap<>();
	public MapPainting painting;
	
	long interval = 1;
	int initCount = 7;
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		
                new BukkitRunnable() {
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
                }.runTaskTimerAsynchronously(painting, interval, interval);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, int x, int y) {
		if(itemStack.getType() == Material.INK_SACK) {
			byte colorValue = (byte)painting.canvas.color
					.getIndex(painting.palette.dye.getColor(itemStack));
			canvas.canvas.paint(x, y, colorValue);
			PencilTickCounter last = lastStroke.get(canvas);
			if(last != null) {
				double dy = y - last.y;
				double dx = x - last.x;
				
				// Using dda.
				if(dx != 0 || dy != 0) 
					if(FastMath.abs(dy) >= FastMath.abs(dx)) {
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
						for(int i = 0; i < FastMath.abs(dy); i ++) 
							canvas.canvas.paint((int) FastMath.round(beginX + 
									diff * i), beginY + i, colorValue);
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
						for(int i = 0; i < FastMath.abs(dx); i ++) 
							canvas.canvas.paint(beginX + i, 
									(int) FastMath.round(beginY + diff * i),  colorValue);
					}
			}
			this.lastStroke.put(canvas, new PencilTickCounter(x, y, initCount));
			
			return true;
		}
		return false;
	}
}
