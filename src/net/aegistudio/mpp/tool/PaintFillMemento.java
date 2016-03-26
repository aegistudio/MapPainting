package net.aegistudio.mpp.tool;

import java.awt.Color;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.algo.CanvasAdapter;
import net.aegistudio.mpp.algo.FillGenerator;
import net.aegistudio.mpp.algo.ScanFloodFillGenerator;
import net.aegistudio.mpp.canvas.Canvas;

/**
 * The paint memoto is not fully revertable.
 * Will be fixed in the follow versions.
 * 
 * @author aegistudio
 */

public class PaintFillMemento implements Memento {
	private final FillGenerator fill = new ScanFloodFillGenerator();
	
	private final Canvas canvas;
	private final Color fillColor;
	private final String fillMessage;
	private final Interaction interact;
	
	public PaintFillMemento(Canvas canvas, Interaction interact, Color c, String fillMessage) {
		this.canvas = canvas;
		this.fillColor = c;
		this.fillMessage = fillMessage;
		this.interact = interact;
	}
	
	private Color seedColor;
	
	@Override
	public void exec() {
		seedColor = this.canvas.look(interact.x, interact.y);
		fill.fill(new CanvasAdapter(interact, fillColor, canvas), interact.x, interact.y, fillColor);
	}

	public String toString() {
		if(this.fillMessage == null) return null;
		return fillMessage.replace("$x", Integer.toString(interact.x))
				.replace("$y", Integer.toString(interact.y))
				.replace("$r", Integer.toString(fillColor.getRed()))
				.replace("$g", Integer.toString(fillColor.getGreen()))
				.replace("$b", Integer.toString(fillColor.getBlue()));
	}
	
	@Override
	public void undo() {
		fill.fill(new CanvasAdapter(interact, seedColor, canvas), interact.x, interact.y, seedColor);
	}
}
