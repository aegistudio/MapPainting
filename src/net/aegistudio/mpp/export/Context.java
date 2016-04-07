package net.aegistudio.mpp.export;

import net.aegistudio.mpp.algo.Paintable;

/**
 * <p>Context is what the plugin canvas can paint on and interact with.</p>
 * <p>You will need the help of asset/algorithm services to paint on the map efficiently.</p>
 * 
 * @see net.aegistudio.mpp.Asset
 * @see net.aegistudio.mpp.AssetManager
 * @see net.aegistudio.mpp.algo
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
