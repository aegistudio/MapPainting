package net.aegistudio.mpp.tool;

import java.awt.Color;

import net.aegistudio.mpp.Memoto;
import net.aegistudio.mpp.canvas.Canvas;

public class PixelTipMemoto implements Memoto {
	private final Canvas canvas;
	private final int i, j;
	private final Color newColor;
	private final String undoMessage;
	
	public PixelTipMemoto(Canvas canvas, int i, int j, Color c, String undoMessage) {
		this.canvas = canvas;
		this.i = i; this.j = j;
		this.newColor = c;
		this.undoMessage = undoMessage;
	}

	private Color oldColor;
	@Override
	public void exec() {
		this.oldColor = canvas.look(i, j);
		canvas.paint(i, j, this.newColor);
	}

	@Override
	public void undo() {
		if(oldColor != null)
			canvas.paint(i, j, oldColor);
	}
	
	public String toString() {
		if(this.undoMessage == null) return null;
		return undoMessage.replace("$x", Integer.toString(i))
				.replace("$y", Integer.toString(j))
				.replace("$r", Integer.toString(newColor.getRed()))
				.replace("$g", Integer.toString(newColor.getGreen()))
				.replace("$b", Integer.toString(newColor.getBlue()));
	}
}
