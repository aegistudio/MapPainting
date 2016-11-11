package net.aegistudio.mpp.export;

/**
 * <p>A plugin canvas registry records the registration of a plugin canvas.</p>
 * 
 * <p>The registration records which plugin and canvas factory generate this
 * plugin canvas, the generated canvas itself, and the mapid and name if
 * the canvas has already been bound.</p>
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
	 * @return the factory instance.
	 */
	public PluginCanvasFactory<T> factory();
	
	/**
	 * @return the plugin canvas instance.
	 */
	public T canvas();
	
	/**
	 * @return the map id if bound, or -1 if not bound.
	 */
	public int mapid();
	
	/**
	 * @return the name if bound, or null if not bound.
	 */
	public String name();
}
