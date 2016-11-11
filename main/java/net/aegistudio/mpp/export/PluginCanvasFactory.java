package net.aegistudio.mpp.export;

/**
 * <p>A virtual constructor for a plugin canvas. The plugin canvas
 * factory should be registered before generating any plugin canvas.<p>
 * 
 * <p>There's one thing to be notice that when registering an factory instance
 * through <code>PluginCanvasService.register</code>, the instance is identified
 * by its name. So you could register multiple factory of the same class but different
 * configuration by giving them different name.</p>
 * 
 * <p>Registering a factory (say <code>A</code>) whose name is already occupied by another factory 
 * instance (say <code>B</code>), but without class conflict (say <code>A.getClass() == B.getClass()</code>) will
 * cause a recreation of the plugin canvas. This is crucial when you reload your plugin
 * without reloading Map Painting, no recreation may cause false reference.
 * 
 * <p>If there's a class conflict (say <code>A.getClass() != B.getClass()</code>), a <code>NamingException</code> will be 
 * thrown out.</p>
 * 
 * @see net.aegistudio.mpp.export.PluginCanvasService#register
 * @author aegistudio
 * 
 * @param <T> which canvas could this factory generate.
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
	
	public T create(Context p);
}