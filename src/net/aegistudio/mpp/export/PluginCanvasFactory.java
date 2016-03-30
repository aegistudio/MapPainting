package net.aegistudio.mpp.export;

/**
 * A virtual constructor for the plugin canvas. The plugin canvas
 * factory should be registered before generating any plugin canvas.
 * 
 * @see PluginCanvasService.register
 * @author aegistudio
 * 
 * @param <T> which plugin canvas could this factory generate.
 */

public interface PluginCanvasFactory <T extends PluginCanvas> {
	/**
	 * Create a plugin canvas. This class should be registered
	 * to plugin canvas service so that the underlying canvas
	 * will be loaded.
	 * 
	 * @param p peripherals that the plugin canvas could access.
	 * 
	 * @return the created plugin canvas.
	 */
	
	public T create(Peripheral p);
}