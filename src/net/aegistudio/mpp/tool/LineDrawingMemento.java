package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.ArrayList;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.Memento;
import net.aegistudio.mpp.algo.DdaLineGenerator;
import net.aegistudio.mpp.algo.LineGenerator;
import net.aegistudio.mpp.algo.Paintable;
import net.aegistudio.mpp.canvas.Canvas;

public class LineDrawingMemento implements Memento, Paintable {
	private final LineGenerator line = new DdaLineGenerator();
	private final Canvas canvas;
	private final int x1, y1, x2, y2;
	private final Color lineColor;
	private final String undoMessage;
	private final Interaction interact;
	
	@Override
	public void color(Color c) {}
	
	public LineDrawingMemento(Canvas canvas, int x1, int y1, int x2, int y2, 
			Color c, String undoMessage, Interaction interact) {
		this.canvas = canvas;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
		this.lineColor = c;
		this.undoMessage = undoMessage;
		this.interact = interact;
	}
	
	public String toString() {
		if(this.undoMessage == null) return null;
		return this.undoMessage.replace("$x1", Integer.toString(x1))
				.replace("$y1", Integer.toString(y1))
				.replace("$x2", Integer.toString(x2))
				.replace("$y2", Integer.toString(y2))
				.replace("$r", Integer.toString(lineColor == null? -1 : lineColor.getRed()))
				.replace("$g", Integer.toString(lineColor == null? -1 : lineColor.getGreen()))
				.replace("$b", Integer.toString(lineColor == null? -1 : lineColor.getBlue()));
	}

	ArrayList<PixelTapMemento> subMemoto;
	@Override
	public void exec() {
		if(subMemoto == null) {
			this.subMemoto = new ArrayList<PixelTapMemento>();
			line.line(this, x1, y1, x2, y2);
		}
		
		for(Memento memoto : this.subMemoto)
			memoto.exec();
	}

	@Override
	public void undo() {
		if(this.subMemoto != null)
			for(Memento memoto : this.subMemoto)
				memoto.undo();
	}

	@Override
	public int size() {
		return canvas.size();
	}

	@Override
	public void set(int x, int y) {
		this.subMemoto.add(new PixelTapMemento(canvas, 
				interact.reCoordinate(x, y), lineColor, null));
	}

	@Override
	public Color get(int x, int y) {
		return canvas.look(x, y);
	}
}
