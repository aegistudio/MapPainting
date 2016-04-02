package net.aegistudio.mpp.algo;

import java.awt.Color;

public interface Paintable {
	public void color(Color c);
	
	public int size();
	
	public void set(int x, int y);
	
	public Color get(int x, int y);
}