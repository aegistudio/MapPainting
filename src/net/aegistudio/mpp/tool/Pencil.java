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
	
	public static final String TIP_MESSAGE = "tipMessage";
	public String tipMessage = "Tip on pixel [$x, $y] with color ($r, $g, $b).";
	
	public static final String LINE_MESSAGE = "lineMessage";
	public String lineMessage = "Draw a line from [$x1, $y1] to [$x2, $y2] with color ($r, $g, $b).";
	
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
		
		this.tipMessage = painting.getLocale(TIP_MESSAGE, tipMessage, section);
		this.lineMessage = painting.getLocale(LINE_MESSAGE, lineMessage, section);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, int x, int y) {
		if(itemStack.getType() == Material.INK_SACK) {
			Color color = painting.palette.dye.getColor(itemStack);
			PencilTickCounter last = lastStroke.get(canvas);
			if(last != null) canvas.history.add(new LineDrawingMemoto(canvas.canvas,
					last.x, last.y, x, y, color, this.lineMessage));
			else canvas.history.add(new PixelTipMemoto(canvas.canvas, x, y, color, this.tipMessage));
			this.lastStroke.put(canvas, new PencilTickCounter(x, y, initCount));
			
			return true;
		}
		return false;
	}
}
