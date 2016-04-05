package net.aegistudio.mpp.export;

import net.aegistudio.mpp.algo.Paintable;

/**
 * Context is what the plugin canvas can
 * paint on and interact with.
 * 
 * @author aegistudio
 */

public interface Context extends Paintable {
	/**
	 * Clear the screen of current context.
	 */
	public void clear();
	
	/**
	 * Force the context to redraw.
	 */
	public void repaint();
}
