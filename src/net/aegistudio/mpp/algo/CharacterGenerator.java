package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.export.Asset;

/**
 * Draw a character of specified font/bitmap.
 * 
 * @author aegistudio
 */

public interface CharacterGenerator extends Asset {
	/**
	 * Generate character with specified bounding box.
	 * @param p where to paint.
	 * @param x1 the x coordinate of start point for bounding box.
	 * @param y1 the y coordinate of start point for bounding box.
	 * @param x2 the x coordinate of end point for bounding box.
	 * @param y2 the y coordinate of end point for bounding box.
	 * @param c the character to paint.
	 */
	public void chargen(Paintable p, int x1, int y1, int x2, int y2, char c);
	
	/**
	 * Generate character at specified position with font size.
	 * @param p where to paint.
	 * @param x the left bottom x coordinate.
	 * @param y the left bottom y coordinate.
	 * @param scale the font size.
	 * @param c the character to paint.
	 * @return the space consumed.
	 */
	public int chargen(Paintable p, int x, int y, float scale, char c);
	
	/**
	 * Metric the width of the font.
	 * @param scale the font size
	 * @param c the character to metric.
	 * @return the width of metric.
	 */
	public int metricWidth(float scale, char c);
	
	/**
	 * Metric the height of the font.
	 * @param scale the font size
	 * @param c the character to metric.
	 * @return the width of metric.
	 */
	public int metricHeight(float scale, char c);
}
