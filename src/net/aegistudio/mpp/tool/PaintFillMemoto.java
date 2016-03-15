package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.ArrayList;

import net.aegistudio.mpp.Memoto;
import net.aegistudio.mpp.canvas.Canvas;

public class PaintFillMemoto implements Memoto {
	private final Canvas canvas;
	private final int i, j;
	private final Color fillColor;
	private final String fillMessage;
	public PaintFillMemoto(Canvas canvas, int i, int j, Color c, String fillMessage) {
		this.canvas = canvas;
		this.i = i; this.j = j;
		this.fillColor = c;
		this.fillMessage = fillMessage;
	}
	
	private Color lookColor;
	
	private ArrayList<Integer> borderX, borderY;
	
	@Override
	public void exec() {
		lookColor = this.canvas.look(i, j);
		if(borderX == null || borderY == null) {
			this.borderX = new ArrayList<Integer>();
			this.borderY = new ArrayList<Integer>();
			this.seedFill(canvas, i, j, fillColor, lookColor, true);
		}
		else this.seedFill(canvas, i, j, fillColor, lookColor, false);
	}

	public String toString() {
		if(this.fillMessage == null) return null;
		return fillMessage.replace("$x", Integer.toString(i))
				.replace("$y", Integer.toString(j))
				.replace("$r", Integer.toString(fillColor.getRed()))
				.replace("$g", Integer.toString(fillColor.getGreen()))
				.replace("$b", Integer.toString(fillColor.getBlue()));
	}
	
	@Override
	public void undo() {
		Color fillColor = this.canvas.look(i, j);
		
		for(int n = 0; n < borderX.size(); n ++) 
			if(borderX.get(n) != i && borderY.get(n) != j)
				canvas.paint(borderX.get(n), borderY.get(n), fillColor);
		
		this.seedFill(canvas, i, j, lookColor, fillColor, false);
	}
	
	private void seedFill(Canvas canvas, int i, int j, Color c, Color seed, boolean updateBorder) {
		if(c == null || seed == null) return;
		if(c.getRGB() == seed.getRGB()) return;
		if(!inRange(canvas, i, j)) return;
		
		canvas.paint(i, j, c);
		
		int xmin = i - 1;
		for(; xmin >= 0; xmin --) {
			Color color = canvas.look(xmin, j);
			if(color == null) break;
			if(color.getRGB() != seed.getRGB()) break;
			canvas.paint(xmin, j, c);
		}
		
		int xmax = i + 1;
		for(; xmax <= canvas.size(); xmax ++) {
			Color color = canvas.look(xmax, j);
			if(color == null) break;
			if(color.getRGB() != seed.getRGB()) break;
			canvas.paint(xmax, j, c);
		}
		
		if(updateBorder) {
			borderX.add(xmin + 1);
			borderY.add(j);
			borderX.add(xmax - 1);
			borderY.add(j);
		}
		
		for(int p = xmin + 1; p < xmax; p ++) {
			Color up = canvas.look(p, j + 1);
			if(up != null) {
				if(up.getRGB() == seed.getRGB())
					seedFill(canvas, p, j + 1, c, seed, updateBorder);
			}
			else if(updateBorder) {
				borderX.add(p); borderY.add(j);
			}
			
			Color down = canvas.look(p, j - 1);
			if(down != null) {
				if(down.getRGB() == seed.getRGB()) 
					seedFill(canvas, p, j - 1, c, seed, updateBorder);
			}
			else if(updateBorder) {
				borderX.add(p); borderY.add(j);
			}
		}
	}
	
	private boolean inRange(Canvas c, int i, int j) {
		if(i < 0) return false;
		if(j < 0) return false;
		if(i >= c.size()) return false;
		if(j >= c.size()) return false;
		return true;
	}
}
