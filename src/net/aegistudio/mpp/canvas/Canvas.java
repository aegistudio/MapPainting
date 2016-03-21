package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.TreeSet;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;

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

	public void add() {}

	public void remove() {}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(!hasViewed(player))
			subrender(view, canvas, player);
	}
	
	protected abstract void subrender(MapView view, MapCanvas canvas, Player player);
	
	protected final TreeSet<Integer> viewed = new TreeSet<Integer>();
	protected final HashSet<Object> observers = new HashSet<Object>();
	public synchronized void repaint() {
		viewed.clear();
		observers.clear();
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