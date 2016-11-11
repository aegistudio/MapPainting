package net.aegistudio.mpp.export;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;

import net.aegistudio.mpp.Interaction;

/**
 * <p>Generalize the features of any plugin-coded canvas. </p>
 * 
 * <p>Any plugin that want to have its customized canvas should register the factory of the canvas.
 * to <code>PluginCanvasService</code> provided by Map Painting.</p>
 * 
 * @see net.aegistudio.mpp.export.PluginCanvasService
 * @see net.aegistudio.mpp.export.PluginCanvasFactory
 * 
 * @author aegistudio
 */

public interface PluginCanvas extends Cloneable {
	/**
	 * Invoked when the user use paint tools to paint on the map.
	 * 
	 * @param i the interaction information.
	 * @param c the used color.
	 * 
	 * @see net.aegistudio.mpp.Interaction
	 */
	public void paint(Interaction i, Color c);
	
	/**
	 * Invoked when the user use hands or non-paint tools to interact with the map.
	 * 
	 * @param i the interaction information.
	 * @return whether there's a interaction, return true if you are not sure.
	 * 
	 * @see net.aegistudio.mpp.Interaction
	 */
	public boolean interact(Interaction i);
	
	/**
	 * Load data from the saved file.
	 * 
	 * @param input the stored canvas data's input stream.
	 */
	public void load(InputStream input);
	
	/**
	 * Save data to the canvas file.
	 * 
	 * @param output the stored canvas data's input stream.
	 */
	public void save(OutputStream output);
	
	/**
	 * <p>Invoked when this canvas was added or loaded into the game.</p>
	 * 
	 * <p>You should be cared that calling <code>PluginCanvasService.generate</code> will
	 * generate a instance of this class but not invoke this method, until you call <code>PluginCanvasService.create</code>
	 * and successfully bind this canvas to a map.</p>
	 * 
	 * @param registry registry that delegates this canvas
	 * 
	 * @see net.aegistudio.mpp.export.PluginCanvasService#generate
	 * @see net.aegistudio.mpp.export.PluginCanvasService#create
	 */
	public void add(PluginCanvasRegistry<? extends PluginCanvas> registry);
	
	/**
	 * Invoked when this canvas was removed or unloaded from the game.
	 * 
	 * @param registry registry that delegates this canvas
	 * 
	 * @see net.aegistudio.mpp.export.PluginCanvasService#destroy
	 */
	public void remove(PluginCanvasRegistry<? extends PluginCanvas> registry);
	
	/**
	 * Called when the canvas ticked.
	 */
	public void tick();
}
