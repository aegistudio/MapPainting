package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class Pencil implements PaintTool {
	public TreeMap<Integer, PencilTickCounter> lastStroke 
		= new TreeMap<Integer, PencilTickCounter>();
	public MapPainting painting;
	
	long interval = 1;
	int initCount = 7;
	
	public static final String TAP_MESSAGE = "tapMessage";
	public String tapMessage = "Tap on pixel [$x, $y] with color ($r, $g, $b).";
	
	public static final String LINE_MESSAGE = "lineMessage";
	public String lineMessage = "Draw a line from [$x1, $y1] to [$x2, $y2] with color ($r, $g, $b).";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		
		painting.getServer().getScheduler().scheduleSyncRepeatingTask(painting, new Runnable() {
			@Override
			public void run() {
				Iterator<Entry<Integer, PencilTickCounter>> 
					counter = lastStroke.entrySet().iterator();
				while(counter.hasNext()) {
					Entry<Integer, PencilTickCounter> current = counter.next();
					current.getValue().count --;
					if(current.getValue().count <= 0)
						counter.remove();
				}
			}
		}, interval, interval);
		
		this.tapMessage = painting.getLocale(TAP_MESSAGE, tapMessage, section);
		this.lineMessage = painting.getLocale(LINE_MESSAGE, lineMessage, section);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
		if(itemStack.getType() == Material.INK_SACK) {
			Color color = painting.palette.dye.getColor(itemStack);
			this.pencilPaint(interact, canvas, color);
			return true;
		}
		return false;
	}
	
	protected void pencilPaint(Interaction interact, MapCanvasRegistry canvas, Color color) {
		Integer entityId = this.getTickCounterKey(interact);
		
		PencilTickCounter last = this.lastStroke.get(entityId);
		if(last != null && last.canvas == canvas) canvas.history.add(new LineDrawingMemento(canvas.canvas,
				last.interaction.x, last.interaction.y, interact.x, interact.y, color, this.lineMessage, interact));
		else canvas.history.add(new PixelTapMemento(canvas.canvas, interact, color, this.tapMessage));
		
		if(entityId != null) this.lastStroke.put(entityId, new PencilTickCounter(interact, canvas, initCount));
	}
	
	protected Integer getTickCounterKey(Interaction interact) {
		if(interact.sender != null && interact.sender instanceof Player)
			return ((Player)interact.sender).getEntityId();
		return null;
	}
}
