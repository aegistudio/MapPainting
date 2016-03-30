package net.aegistudio.mpp.export;

import net.aegistudio.mpp.canvas.Graphic;

/**
 * Peripheral is what the plugin canvas is 
 * permitted to interact with.
 * 
 * @author aegistudio
 */

public interface Peripheral {
	/**
	 * The plugin canvases can paint on
	 * graphic instances.
	 * 
	 * @return the graphic instance.
	 */
	public Graphic getGraphic();
}
