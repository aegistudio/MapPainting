package net.aegistudio.mpp.canvas;

import java.util.ArrayList;
import java.awt.Color;
import org.bukkit.map.MapPalette;

public class CanvasColor {
	public Color[] mapColorLookup;
	
	@SuppressWarnings("deprecation")
	public CanvasColor() {
		ArrayList<Color> colorLookup = new ArrayList<Color>();
		try {
			for(int i = 0; i < 256; i ++) {
				Color color = MapPalette.getColor((byte) i);
				colorLookup.add(color);
			}
		}
		catch(Throwable t) {		}
		mapColorLookup = colorLookup.toArray(new Color[0]);
	}
	
	// Actually it can sometimes use some caching control.
	public int getIndex(Color color) {
		if(color == null) return 0;
		int distance = Integer.MAX_VALUE;
		int picked = 0;
		for(int i = 4; i < mapColorLookup.length; i ++) {
			Color oldColor = mapColorLookup[i];
			int red = color.getRed() - oldColor.getRed();
			int green = color.getGreen() - oldColor.getGreen();
			int blue = color.getBlue() - oldColor.getBlue();
			int newDistance = red * red + green * green + blue * blue;
			if(newDistance < distance) {
				distance = newDistance;
				picked = i;
			}
		}
		return picked;
	}
	
	public Color getColor(byte index) {
		int i = index;	if(i < 0) i += 256;
		return mapColorLookup[i];
	}
}
