package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.ArrayList;

import net.aegistudio.mpp.Memoto;
import net.aegistudio.mpp.canvas.Canvas;

public class LineDrawingMemoto implements Memoto {
	private final Canvas canvas;
	private final int x1, y1, x2, y2;
	private final Color fillColor;
	private final String undoMessage;
	
	public LineDrawingMemoto(Canvas canvas, int x1, int y1, int x2, int y2, Color c, String undoMessage) {
		this.canvas = canvas;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
		this.fillColor = c;
		this.undoMessage = undoMessage;
	}
	
	public String toString() {
		if(this.undoMessage == null) return null;
		return this.undoMessage.replace("$x1", Integer.toString(x1))
				.replace("$y1", Integer.toString(y1))
				.replace("$x2", Integer.toString(x2))
				.replace("$y2", Integer.toString(y2))
				.replace("$r", Integer.toString(fillColor.getRed()))
				.replace("$g", Integer.toString(fillColor.getGreen()))
				.replace("$b", Integer.toString(fillColor.getBlue()));
	}

	ArrayList<PixelTipMemoto> subMemoto;
	@Override
	public void exec() {
		if(subMemoto == null) {
			this.subMemoto = new ArrayList<PixelTipMemoto>();
			
			double dy = y2 - y1;
			double dx = x2 - x1;
			
			// Using dda.
			if(dx != 0 || dy != 0) 
				if(Math.abs(dy) >= Math.abs(dx)) {
					int beginX, beginY;
					
					if(dy <= 0) {
						beginX = x2;
						beginY = y2;
					}
					else {
						beginX = x1;
						beginY = y1;
					}
					
					double diff = dx / dy;
					for(int i = 0; i < Math.abs(dy); i ++) 
						this.subMemoto.add(new PixelTipMemoto(canvas, (int) Math.round(beginX + 
								diff * i), beginY + i, fillColor, null));
				}
				else {
					int beginX, beginY;
					
					if(dx <= 0) {
						beginX = x2;
						beginY = y2;
					}
					else {
						beginX = x1;
						beginY = y1;
					}
					
					double diff = dy / dx;
					for(int i = 0; i < Math.abs(dx); i ++) 
						this.subMemoto.add(new PixelTipMemoto(canvas, beginX + i,
								(int) Math.round(beginY + diff * i), fillColor, null));
				}
		}
		
		for(Memoto memoto : this.subMemoto)
			memoto.exec();
	}

	@Override
	public void undo() {
		if(this.subMemoto != null)
			for(Memoto memoto : this.subMemoto)
				memoto.undo();
	}
}
