package net.aegistudio.mpp.tool;

import java.awt.Color;

import net.aegistudio.mpp.Memoto;
import net.aegistudio.mpp.canvas.Canvas;

public class PaintFillMemoto implements Memoto {
	private final Canvas canvas;
	private final int i, j;
	private final Color fillColor;
	private final String undoMessage;
	public PaintFillMemoto(Canvas canvas, int i, int j, Color c, String undoMessage) {
		this.canvas = canvas;
		this.i = i; this.j = j;
		this.fillColor = c;
		this.undoMessage = undoMessage.replace("$x", Integer.toString(i))
				.replace("$y", Integer.toString(j))
				.replace("$r", Integer.toString(c.getRed()))
				.replace("$g", Integer.toString(c.getGreen()))
				.replace("$b", Integer.toString(c.getBlue()));
	}
	
	private Color lookColor;
	@Override
	public void exec() {
		lookColor = this.canvas.look(i, j);
		this.seedFill(canvas, i, j, fillColor, lookColor);
	}

	public String toString() {
		return undoMessage;
	}
	
	@Override
	public void undo() {
		Color fillColor = this.canvas.look(i, j);
		this.seedFill(canvas, i, j, lookColor, fillColor);
	}
	
	private void seedFill(Canvas canvas, int i, int j, Color c, Color seed) {
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
		
		for(int p = xmin + 1; p < xmax; p ++) {
			Color up = canvas.look(p, j + 1);
			if(up != null) {
				if(up.getRGB() == seed.getRGB())
					seedFill(canvas, p, j + 1, c, seed);
			}
			
			Color down = canvas.look(p, j - 1);
			if(down != null) {
				if(down.getRGB() == seed.getRGB()) 
					seedFill(canvas, p, j - 1, c, seed);
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
