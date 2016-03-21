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

public class WrapCanvas extends Canvas {
	
	@Override
	public void load(MapPainting painting, InputStream input) throws Exception {
		this.painting = painting;
		DataInputStream din = new DataInputStream(input);
		this.wrapping = din.readUTF();
	}
	
	@Override
	public void save(MapPainting painting, OutputStream output) throws Exception {
		DataOutputStream dout = new DataOutputStream(output);
		dout.writeUTF(this.wrapping);
	}
	
	private String wrapping = "";
	public MapPainting painting;
	
	@Override
	public void paint(Interaction interact, Color color) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return;
		if(interact.sender != null)
			if(!wrapped.painter.contains(interact.sender.getName())) return;
		wrapped.canvas.paint(interact, color);
	}

	@Override
	public Color look(int x, int y) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return null;
		return wrapped.canvas.look(x, y);
	}

	@Override
	public boolean interact(Interaction interact) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return false;
		if(!interact.sender.hasPermission("mpp.interact")) return false;
		return wrapped.canvas.interact(interact);
	}

	@Override
	public int size() {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return 0;
		return wrapped.canvas.size();
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped != null) 
			if(!wrapped.canvas.hasObserver(this)) 
				repaint();
		super.render(view, canvas, player);
	}
	
	public WrapCanvas clone() {
		WrapCanvas newCanvas = new WrapCanvas();
		newCanvas.painting = painting;
		newCanvas.wrapping = wrapping;
		return newCanvas;
	}
	
	public void setWrapping(String newWrapping) {
		this.wrapping = newWrapping;
		this.repaint();
	}

	@Override
	protected void subrender(MapView view, MapCanvas canvas, Player player) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return;
		wrapped.canvas.subrender(view, canvas, player);
	}
}
