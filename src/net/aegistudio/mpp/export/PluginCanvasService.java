package net.aegistudio.mpp.export;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * This interface shows you what map painting could be used in foreign plugin
 * cases.
 * 
 * @author aegistudio
 */

public interface PluginCanvasService {
	/**
	 * Register a canvas factory. Please notice that a registry is identified by 2 elements,
	 * the plugin, and the identifier. Which means a single factory class may have more than one
	 * factory instance, for a specified identifier.
	 * 
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @param factory the canvas factory instance.
	 */
	public <T extends PluginCanvas> void register(Plugin thiz, String identifier, PluginCanvasFactory<T> factory);
	
	/**
	 * Get all canvases that belongs to the same identifier.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @return the canvas name to the canvas instance.
	 */
	public <T extends PluginCanvas> Map<String, PluginCanvasRegistry<T>> getPluginCanvases(Plugin thiz, 
			String identifier, Class<T> canvasClazz);

	/**
	 * Generate a plugin canvas for this plugin.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas.
	 * @param clazz which class does the canvas belongs to.
	 * @return the generated plugin registry.
	 * @throws NamingException when the corresponding identifier not registered.
	 */
	public <T extends PluginCanvas> PluginCanvasRegistry<T> generate(Plugin thiz, String identifier, 
			Class<T> clazz) throws NamingException;
	
	/**
	 * 
	 * @param mapid the map for creating the canvas.
	 * @param owner the owner of the canvas. only this plugin could manipulate it when set to null.
	 * @param name the name of the canvas.
	 * @param registry a suitable creation of the canvas.
	 * @throws NamingException thrown when the canvas is occupied.
	 */
	public <T extends PluginCanvas> void create(short mapid, CommandSender owner, 
			String name, PluginCanvasRegistry<T> registry) throws NamingException ;
	
	/**
	 * Destroy a canvas.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @param name the name of the canvas.
	 * 
	 * @return does the destroy success?
	 */
	public boolean destroy(Plugin thiz, String identifier, String name);
}
