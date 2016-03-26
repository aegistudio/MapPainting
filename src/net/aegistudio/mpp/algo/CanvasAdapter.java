package net.aegistudio.mpp.algo;

import java.awt.Color;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.canvas.Canvas;

public class CanvasAdapter implements Paintable {
	public final Interaction interact;
	public final Canvas canvas;
	public final Color color;
	public CanvasAdapter(Interaction interact, Color color, Canvas canvas) {
		this.interact = interact;
		this.canvas = canvas;
		this.color = color;
	}
	
	@Override
	public int size() {
		return canvas.size();
	}
	
	@Override
	public void set(int x, int y) {
		canvas.paint(interact.reCoordinate(x, y), color);
	}
	
	@Override
	public Color get(int x, int y) {
		return canvas.look(x, y);
	}
}
