package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.map.MapView;

import net.aegistudio.mpp.algo.Paintable;

public class Graphic implements Paintable {
	private Canvas canvas;
	
	public boolean dirty;
	public int rowMax, colMax;
	public int rowMin, colMin;
	
	public Graphic(Canvas canvas) {
		this.canvas = canvas;
		this.waste();
	}
	
	public void clean() {
		this.dirty = false;
		this.colMax = 0;
		this.rowMax = 0;
		this.colMin = 127;
		this.rowMin = 127;
	}
	
	public void waste() {
		this.dirty = true;
		this.colMax = 127;
		this.rowMax = 127;
		this.colMin = 0;
		this.rowMin = 0;
	}
	
	public int index(int x, int y) {
		return (127 - y) * 128 + x;
	}
	
	public final byte[] pixel = new byte[128 * 128];

	public byte color;
	@Override
	public void bcolor(byte c) {
		this.color = c;
	}
	
	public void color(Color color) {
		this.bcolor((byte) canvas.painting.canvas.color.getIndex(color));
	}
	
	public void clear() {
		for(int i = 0; i < 128 * 128; i ++)
			pixel[i] = color;
		waste();
	}
	
	public void clear(Color clearColor) {
		this.color(clearColor);
		this.clear();
	}

	public void set(int x, int y) {
		if(x >= 128 || x < 0) return;
		if(y >= 128 || y < 0) return;
		if(pixel[index(x, y)] != color) {
			pixel[index(x, y)] = color;	
			modify(x, y);
		}
	}
	
	public void modify(int x, int y) {
		int row = 127 - y; int col = x;
		
		this.dirty = true;
		if(col < colMin) colMin = col;
		if(col > colMin) colMin = col;
		if(row < rowMin) rowMin = row;
		if(row > rowMax) rowMax = row;
	}
	
	public void set(int x, int y, Color color) {
		this.color(color);
		this.set(x, y);
	}
	
	public Color get(int x, int y) {
		return canvas.painting.canvas.color.getColor(this.bget(x, y));
	}
	
	@Override
	public byte bget(int x, int y) {
		if(x >= 128 || x < 0) return 0;
		if(y >= 128 || y < 0) return 0;
		return pixel[index(x, y)];
	}
	
	public void subrender(MapView view, Paintable canvas) {
		for(int i = 0; i < 128; i ++)
			for(int j = 0; j < 128; j ++) {
				canvas.bcolor(pixel[index(i, j)]);
				canvas.set(i, j);
			}
	}
	
	public void repaint() {
		this.canvas.repaint();
	}
	
	public void read(DataInputStream din) throws IOException {
		byte[] buffer = new byte[128];
		for(int i = 0; i < 128; i ++) {
			din.readFully(buffer);
			System.arraycopy(buffer, 0, this.pixel, 128 * (127 - i), 128);
		}
		din.readByte();	// Inteded for extension.
	}
	
	public void write(DataOutputStream dout) throws IOException {
		byte[] buffer = new byte[128];
		for(int i = 0; i < 128; i ++) {
			System.arraycopy(this.pixel, (127 - i) * 128, buffer, 0, 128);
			dout.write(buffer);
		}
		dout.writeByte(0); // Intended for extension.
	}

	@Override
	public int width() {
		return 128;
	}
	
	@Override
	public int height() {
		return 128;
	}
	
	public void copy(Graphic another) {
		System.arraycopy(this.pixel, 0, another.pixel, 0, 128 * 128);
		another.waste();
	}
}
