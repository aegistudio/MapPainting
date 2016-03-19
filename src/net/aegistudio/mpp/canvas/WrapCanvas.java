package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.MapPainting;

@Deprecated
public class WrapCanvas extends MapRenderer implements Canvas {
	
	@Override
	public void load(MapPainting painting, InputStream input) throws Exception {	}
	
	@Override
	public void save(MapPainting painting, OutputStream output) throws Exception {	}
	
	public Canvas underlyingCanvas;
	
	@Override
	public void paint(int x, int y, Color color) {
		underlyingCanvas.paint(x, y, color);
	}

	@Override
	public Color look(int x, int y) {
		return underlyingCanvas.look(x, y);
	}

	@Override
	public boolean interact(int x, int y, Player player) {
		return underlyingCanvas.interact(x, y, player);
	}

	@Override
	public MapRenderer getRenderer() {
		return this;
	}

	@Override
	public int size() {
		return underlyingCanvas.size();
	}

	@Override
	public void render(MapView arg0, MapCanvas arg1, Player arg2) {
		this.underlyingCanvas.getRenderer().render(arg0, arg1, arg2);
	}
	
	public WrapCanvas clone() {
		return new WrapCanvas();
	}
}
