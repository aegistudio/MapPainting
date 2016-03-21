package net.aegistudio.mpp.canvas;

import java.awt.Color;

public class CachedCanvasColor extends CanvasColor {
	private int[] color;
	private int[] mapped;
	public CachedCanvasColor(int count) {
		this.color = new int[count];
		this.mapped = new int[count];
		for(int i = 0; i < count; i ++)	{
			this.color[i] = 0;
			this.mapped[i] = -1;
		}
	}
	
	public int getIndex(Color color) {
		if(color == null) return 0;
		int rgbValue = color.getRGB();
		int hashed = Math.abs(rgbValue % this.color.length);
		if(this.color[hashed] == rgbValue)	{
			if(mapped[hashed] == -1)
				mapped[hashed] = super.getIndex(color);
			return mapped[hashed];
		}
		else {
			this.color[hashed] = rgbValue;
			return mapped[hashed] = super.getIndex(color);
		}
	}
}
