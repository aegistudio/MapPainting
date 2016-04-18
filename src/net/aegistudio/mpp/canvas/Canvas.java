package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.MapCanvasAdapter;
import net.aegistudio.mpp.algo.Paintable;

public abstract class Canvas extends MapRenderer implements Cloneable {
	public final MapPainting painting;
	public Canvas(MapPainting painting) {
		this.painting = painting;
	}
	
	public abstract void load(MapPainting painting, InputStream mppFile) throws Exception;

	public abstract void paint(Interaction interact, Color color);
	
	public abstract Color look(int x, int y);

	public abstract boolean interact(Interaction interact);

	public abstract int size();

	public abstract void save(MapPainting painting, OutputStream mppFile) throws Exception;
	
	public abstract Canvas clone();
	
	public void place(Location blockLocation, BlockFace face) {			}
	
	public void unplace(Item dropped) {			}
	
	private Graphic context = new Graphic(this);
	protected void tick() {
		if(updateDisplay) {
			this.subrender(view, context);
			updateDisplay = false;
		}
		
		Iterator<Entry<Player, Integer>> suspector = this.suspector.entrySet().iterator();
		while(suspector.hasNext()) {
			Entry<Player,Integer> entry = suspector.next();
			entry.setValue(entry.getValue() + 1);
			if(entry.getValue() >= painting.canvas.suspectTimedOut) 
				suspector.remove();
			else if(context.dirty) 
				painting.sender.sendPacket(entry.getKey(), view, context);
		}
		context.clean();
	}
	
	protected BukkitRunnable tickRunnable = new BukkitRunnable() {
		@Override
		public void run() {
			tick();
		}
	};

	protected MapView view;
	public void add(MapCanvasRegistry registry) {
		tickRunnable.runTaskTimer(painting, 1, 1);
		this.view = registry.view;
	}

	public void remove(MapCanvasRegistry registry) {
		tickRunnable.cancel();
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(!hasViewed(player)) 
			context.subrender(view, 
					new MapCanvasAdapter(painting.canvas.color, canvas));
		suspector.put(player, 0);
	}
	
	protected abstract void subrender(MapView view, Paintable canvas);
	
	protected final TreeSet<Integer> viewed = new TreeSet<Integer>();
	protected final HashSet<Object> observers = new HashSet<Object>();
	protected final HashMap<Player, Integer> suspector = new HashMap<Player, Integer>();
	
	public boolean updateDisplay = true;
	public synchronized void repaint() {
		viewed.clear();
		observers.clear();
		updateDisplay = true;
	}

	public synchronized boolean hasViewed(Player player) {
		if(player == null) return false;
		if(this.viewed.contains(player.getEntityId())) return true;
		this.viewed.add(player.getEntityId());
		return false;
	}
	
	public synchronized boolean hasObserver(Object observer) {
		if(this.observers.contains(observer)) return true;
		this.observers.add(observer);
		return false;
	}
}