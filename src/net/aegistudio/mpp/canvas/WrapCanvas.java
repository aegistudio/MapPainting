package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.InteractInfo;
import net.aegistudio.mpp.MapPainting;

public class WrapCanvas extends MapRenderer implements Canvas {
	
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
	
	public String wrapping = "";
	public MapPainting painting;
	
	@Override
	public void paint(InteractInfo interact, Color color) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return ;
		if(interact.player != null)
			if(!wrapped.painter.contains(interact.player.getName())) return;
		wrapped.canvas.paint(interact, color);
	}

	@Override
	public Color look(int x, int y) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return null;
		return wrapped.canvas.look(x, y);
	}

	@Override
	public boolean interact(InteractInfo interact) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return false;
		if(!interact.player.hasPermission("mpp.interact")) return false;
		return wrapped.canvas.interact(interact);
	}

	@Override
	public MapRenderer getRenderer() {
		return this;
	}

	@Override
	public int size() {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped == null) return 0;
		return wrapped.canvas.size();
	}

	@Override
	public void render(MapView arg0, MapCanvas arg1, Player arg2) {
		MapCanvasRegistry wrapped = painting.canvas.nameCanvasMap.get(wrapping);
		if(wrapped != null)
			wrapped.canvas.getRenderer().render(arg0, arg1, arg2);
	}
	
	public WrapCanvas clone() {
		WrapCanvas newCanvas = new WrapCanvas();
		newCanvas.painting = painting;
		newCanvas.wrapping = wrapping;
		return newCanvas;
	}
}
