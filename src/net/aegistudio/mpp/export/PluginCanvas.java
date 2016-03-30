package net.aegistudio.mpp.export;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;

import net.aegistudio.mpp.Interaction;

/**
 * The abstract definition of a plugin canvas.
 * Any plugin that want to display a canvas to
 * its user should realize this class.
 * 
 * @author aegistudio
 */

public interface PluginCanvas extends Cloneable {
	/**
	 * When the user use paint tools to paint
	 * on the map, this method will be invoked.
	 * 
	 * @param i the interaction information.
	 * @param c the used color.
	 */
	public void paint(Interaction i, Color c);
	
	/**
	 * When the user use hands or non-paint
	 * tools on the map, this method will be
	 * invoked.
	 * 
	 * @param i the interaction information.
	 * @return whether there's a interaction.
	 */
	public boolean interact(Interaction i);
	
	/**
	 * Load data from the saved file.
	 * @param input the stored canvas data.
	 */
	public void load(InputStream input);
	
	/**
	 * Save data to the canvas file.
	 * @param output the stored canvas data.
	 */
	public void save(OutputStream output);
	
	/**
	 * Called at the moment when this canvas 
	 * was added or loaded into the game.
	 */
	public void add();
	
	/**
	 * Called at the moment when this canvas
	 * was removed or unloaded from the game.
	 */
	public void remove();
}
