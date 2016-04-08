package net.aegistudio.mpp.algo;

import java.awt.Color;

import net.aegistudio.mpp.export.Asset;

/**
 * Fill an enclosed region with specified color.
 * 
 * @author aegistudio
 */

public interface FillGenerator extends Asset {
	/**
	 * Generate filling.
	 * @param p where to paint
	 * @param x the seed coordinate x.
	 * @param y the seed coordinate y.
	 * @param fill the color to fill.
	 */
	public void fill(Paintable p, int x, int y, Color fill);
}
