package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;

/**
 * Provides a pre-implemented interface for wrappers.
 * It's ideal for wrapping canvas management!
 * 
 * @author aegistudio
 */

public class WrapCanvas extends Canvas implements CanvasWrapper {
	
	@Override
	public void load(MapPainting painting, InputStream input) throws Exception {
		this.painting = painting;
		DataInputStream din = new DataInputStream(input);
		this.setWrapping(0, din.readUTF());
	}
	
	@Override
	public void save(MapPainting painting, OutputStream output) throws Exception {
		DataOutputStream dout = new DataOutputStream(output);
		dout.writeUTF(this.wrapping);
	}
	
	public MapPainting painting;
	
	@Override
	public void paint(Interaction interact, Color color) {
		retrieve();
		if(wrappedCanvas == null) return;
		if(interact.sender != null)
			if(!wrappedCanvas.painter.contains(interact.sender.getName())) return;
		wrappedCanvas.canvas.paint(interact, color);
	}

	@Override
	public Color look(int x, int y) {
		retrieve();
		if(wrappedCanvas == null) return null;
		return wrappedCanvas.canvas.look(x, y);
	}

	@Override
	public boolean interact(Interaction interact) {
		retrieve();
		if(wrappedCanvas == null) return false;
		if(!interact.sender.hasPermission("mpp.interact")) return false;
		return wrappedCanvas.canvas.interact(interact);
	}

	@Override
	public int size() {
		retrieve();
		if(wrappedCanvas == null) return 0;
		return wrappedCanvas.canvas.size();
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		retrieve();
		if(wrappedCanvas != null) 
			if(!wrappedCanvas.canvas.hasObserver(this)) 
				repaint();
		super.render(view, canvas, player);
	}

	private String wrapping = "";
	private int currentCount = -1;
	private MapCanvasRegistry wrappedCanvas;
	
	public WrapCanvas clone() {
		WrapCanvas newCanvas = new WrapCanvas();
		this.copy(newCanvas);
		return newCanvas;
	}
	
	protected void copy(WrapCanvas another) {
		another.painting = painting;
		another.wrapping = wrapping;
		another.wrappedCanvas = wrappedCanvas;
		another.currentCount = currentCount;
	}
	
	public void retrieve() {
		if(currentCount < painting.canvas.count)
			if(wrappedCanvas == null) {
				wrappedCanvas = painting.canvas.nameCanvasMap.get(wrapping);
				if(wrappedCanvas != null) repaint();
			}
		
		if(currentCount > painting.canvas.count)
			if(wrappedCanvas != null) {
				if(wrappedCanvas.removed()) {
					wrappedCanvas = null;
					repaint();
				}
			}
		
		currentCount = painting.canvas.count;
	}
	
	public void setWrapping(int layer, String newWrapping) {
		if(this.wrapping != null)
			if(this.wrapping.equals(newWrapping)) return;
		
		this.wrapping = newWrapping;
		this.currentCount = -1;
		this.wrappedCanvas = null;
		this.retrieve();
	}

	@Override
	protected void subrender(MapView view, MapCanvas canvas, Player player) {
		if(wrappedCanvas == null) return;
		wrappedCanvas.canvas.subrender(view, canvas, player);
	}
}
