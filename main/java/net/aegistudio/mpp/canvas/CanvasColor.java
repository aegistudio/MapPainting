
package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.util.ArrayList;
import org.bukkit.map.MapPalette;

public class CanvasColor {
    public Color[] mapColorLookup;

    public CanvasColor() {
        ArrayList<Color> colorLookup = new ArrayList<Color>();
        try {
            for (int i = 0; i < 256; ++i) {
                Color color = MapPalette.getColor((byte)((byte)i));
                colorLookup.add(color);
                
            }
        }
        catch (Throwable i) {
            // empty catch block
        }
        this.mapColorLookup = colorLookup.toArray(new Color[0]);
    }

    public int getIndex(Color color) {
        if (color == null) {
            return 0;
        }
        int distance = Integer.MAX_VALUE;
        int picked = 0;
        for (int i = 4; i < this.mapColorLookup.length; ++i) {
            int green;
            int blue;
            Color oldColor = this.mapColorLookup[i];
            int red = color.getRed() - oldColor.getRed();
            int newDistance = red * red + (green = color.getGreen() - oldColor.getGreen()) * green + (blue = color.getBlue() - oldColor.getBlue()) * blue;
            if (newDistance >= distance) continue;
            distance = newDistance;
            picked = i;
        }
        return picked;
    }

    public Color getColor(byte index) {
        int i = index;
        if (i < 0) {
            i += 256;
        }
        return this.mapColorLookup[i];
    }
}

