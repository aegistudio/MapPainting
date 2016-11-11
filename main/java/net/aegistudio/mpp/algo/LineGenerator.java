package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.export.Asset;

/**
 * Draw a line between two specified points.
 * 
 * @author aegistudio
 */

public interface LineGenerator extends Asset {
	/**
	 * Draw a line.
	 * @param p where to paint.
	 * @param x1 the x coordinate of one point.
	 * @param y1 the y coordinate of one point.
	 * @param x2 the x coordinate of another point.
	 * @param y2 the y coordinate of another point.
	 */
	public void line(Paintable p, int x1, int y1, int x2, int y2);
}
