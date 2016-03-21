package net.aegistudio.mpp.tool;

import java.awt.Color;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.canvas.Canvas;

public class PixelTapMemento implements Memento {
	private final Canvas canvas;
	private final Color newColor;
	private final String undoMessage;
	private final Interaction interact;
	
	public PixelTapMemento(Canvas canvas, Interaction interact, Color c, String undoMessage) {
		this.canvas = canvas;
		this.newColor = c;
		this.undoMessage = undoMessage;
		this.interact = interact;
	}

	private Color oldColor;
	@Override
	public void exec() {
		this.oldColor = canvas.look(interact.x, interact.y);
		canvas.paint(interact, this.newColor);
	}

	@Override
	public void undo() {
		canvas.paint(interact, oldColor);
	}
	
	public String toString() {
		if(this.undoMessage == null) return null;
		return undoMessage.replace("$x", Integer.toString(interact.x))
				.replace("$y", Integer.toString(interact.y))
				.replace("$r", Integer.toString(newColor == null? -1 : newColor.getRed()))
				.replace("$g", Integer.toString(newColor == null? -1 : newColor.getGreen()))
				.replace("$b", Integer.toString(newColor == null? -1 : newColor.getBlue()));
	}
}
