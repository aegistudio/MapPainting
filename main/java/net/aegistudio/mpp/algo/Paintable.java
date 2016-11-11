package net.aegistudio.mpp.algo;

import java.awt.Color;

/**
 * <p>Paintable is the consumer of graphic algorithms.</p>
 * 
 * <p>A graphic algorithm always require a paintable instance
 * as the target to paint, analogous to the abstraction in
 * bridge pattern (design pattern).</p>
 * 
 * @author aegistudio
 */

public interface Paintable {
	/**
	 * Set the brush color.
	 * @param c the brush color in Minecraft map color index form.
	 */
	public void bcolor(byte c);
	
	/**
	 * Set the brush color.
	 * @param c the brush color in AWT color form.
	 */
	public void color(Color c);
	
	/**
	 * Get the width of this paintable.
	 * @return the width of this paintable.
	 */
	public int width();
	
	/**
	 * Get the height of this paintable.
	 * @return the height of this paintable.
	 */
	public int height();
	
	/**
	 * Paint with previous specified brush color.
	 * @param x the x coordinate of pixel to paint.
	 * @param y the y coordinate of pixel to paint.
	 */
	public void set(int x, int y);
	
	/**
	 * Get the color of specified pixel.
	 * @param x the x coordinate of the pixel.
	 * @param y the y coordinate of the pixel.
	 * @return the color in AWT color form.
	 */
	public Color get(int x, int y);
	
	
	/**
	 * Get the color of specified pixel.
	 * @param x the x coordinate of the pixel.
	 * @param y the y coordinate of the pixel.
	 * @return the color in Minecraft map color index form.
	 */
	public byte bget(int x, int y);
}