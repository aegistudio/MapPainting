package net.aegistudio.mpp.export;

/**
 * Represents a plugin canvas registry, which could be used
 * for creation.
 * 
 * @author aegistudio
 *
 * @param <T> the underlying plugin canvas.
 */

public interface PluginCanvasRegistry<T extends PluginCanvas> {
	/**
	 * @return which plugin does this plugin belong to. (plugin name)
	 */
	public String plugin();
	
	/**
	 * @return which factory is used to create this canvas.
	 */
	public String identifier();
	
	/**
	 * @return factory instance that has created this canvas.
	 */
	public PluginCanvasFactory<T> factory();
	
	/**
	 * @return the plugin canvas instance.
	 */
	public T canvas();
}
