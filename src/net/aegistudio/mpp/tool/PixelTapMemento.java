package net.aegistudio.mpp.tool;

import java.awt.Color;

import net.aegistudio.mpp.InteractInfo;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.canvas.Canvas;

public class PixelTapMemento implements Memento {
	private final Canvas canvas;
	private final Color newColor;
	private final String undoMessage;
	private final InteractInfo interact;
	
	public PixelTapMemento(Canvas canvas, InteractInfo interact, Color c, String undoMessage) {
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
		if(oldColor != null)
			canvas.paint(interact, oldColor);
	}
	
	public String toString() {
		if(this.undoMessage == null) return null;
		return undoMessage.replace("$x", Integer.toString(interact.x))
				.replace("$y", Integer.toString(interact.y))
				.replace("$r", Integer.toString(newColor.getRed()))
				.replace("$g", Integer.toString(newColor.getGreen()))
				.replace("$b", Integer.toString(newColor.getBlue()));
	}
}
