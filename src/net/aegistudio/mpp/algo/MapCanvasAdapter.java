package net.aegistudio.mpp.algo;

import java.awt.Color;

import org.bukkit.map.MapCanvas;

import net.aegistudio.mpp.canvas.CanvasColor;

public class MapCanvasAdapter implements Paintable {
	public CanvasColor canvasColor;
	public MapCanvas canvas;
	public MapCanvasAdapter(CanvasColor canvasColor, MapCanvas canvas) {
		this.canvasColor = canvasColor;
		this.canvas = canvas;
	}
	
	byte color = 0;
	@Override
	public void bcolor(byte c) {
		this.color = c;
	}

	@Override
	public void color(Color c) {
		color = (byte) canvasColor.getIndex(c);
	}

	@Override
	public int size() {
		return 128;
	}

	@Override
	public void set(int x, int y) {
		this.canvas.setPixel(x, 127 - y, color);
	}

	@Override
	public Color get(int x, int y) {
		return canvasColor.getColor(this.bget(x, y));
	}
	
	@Override
	public byte bget(int x, int y) {
		return this.canvas.getPixel(x, 127 - y);
	}
}
