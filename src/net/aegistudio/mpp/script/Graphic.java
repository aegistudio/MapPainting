package net.aegistudio.mpp.script;

import java.awt.Color;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class Graphic {
	private ScriptCanvas canvas;
	public Graphic(ScriptCanvas canvas) {
		this.canvas = canvas;
	}
	
	public byte[][] pixel = new byte[128][128];
	public void clear(Color clearColor) {
		byte value = (byte) canvas.painting.canvas.color.getIndex(clearColor);
		for(int i = 0; i < 128; i ++)
			for(int j = 0; j < 128; j ++)
				pixel[i][j] = value;
	}
	
	public void set(int x, int y, Color color) {
		if(x >= 128 || x < 0) return;
		if(y >= 128 || y < 0) return;
		pixel[x][y] = (byte) canvas.painting.canvas.color.getIndex(color);
	}
	
	public Color get(int x, int y) {
		if(x >= 128 || x < 0) return null;
		if(y >= 128 || y < 0) return null;
		return canvas.painting.canvas.color.getColor(pixel[x][y]);
	}
	
	public void subrender(MapView view, MapCanvas canvas, Player player) {
		for(int i = 0; i < 128; i ++)
			for(int j = 0; j < 128; j ++)
				canvas.setPixel(i, j, pixel[i][j]);
	}
	
	public void repaint() {
		this.canvas.repaint();
	}
}