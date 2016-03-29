package net.aegistudio.mpp.export;

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