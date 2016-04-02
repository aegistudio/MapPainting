package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.Asset;

public interface StringGenerator extends Asset {
	/**
	 * Draw a string.
	 * @param p a paintable.
	 * @param x the x position of the string.
	 * @param y the y position of the string.
	 * @param scale the font size of the string.
	 * @param content the string to be displayed.
	 * @return the length of the string.
	 */
	
	public int string(Paintable p, int x, int y, float scale, String content);
}
