package net.aegistudio.mpp.export;

import net.aegistudio.mpp.algo.Paintable;

/**
 * Peripheral is what the plugin canvas is 
 * permitted to interact with.
 * 
 * @author aegistudio
 */

public interface Peripheral extends Paintable {
	/**
	 * Clear the screen of current context.
	 */
	public void clear();
	
	/**
	 * Force the context to redraw.
	 */
	public void repaint();
}
