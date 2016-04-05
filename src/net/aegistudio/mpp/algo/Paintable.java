package net.aegistudio.mpp.algo;

import java.awt.Color;

public interface Paintable {
	public void bcolor(byte c);
	
	public void color(Color c);
	
	public int width();
	
	public int height();
	
	public void set(int x, int y);
	
	public Color get(int x, int y);
	
	public byte bget(int x, int y);
}