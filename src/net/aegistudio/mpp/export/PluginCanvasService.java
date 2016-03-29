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
	 * @throws NamingOccupiedException thrown when the identifier has already been registered.
	 */
	public <T extends PluginCanvas> void register(Plugin thiz, String identifier, PluginCanvasFactory<T> factory);
	
	/**
	 * Get all canvases that belongs to the same identifier.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @return the canvas name to the canvas instance.
	 */
	public <T extends PluginCanvas> Map<String, T> getPluginCanvases(Plugin thiz, String identifier, Class<T> canvasClazz);
	
	/**
	 * Create a canvas.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @param mapid the id of the map to be bound. Will throw conflict exception when already exists.
	 * @param owner the owner of the canvas. Only the plugin could manipulate it when null.
	 * @param name the name of the canvas. Will throw conflict exception when already exists.
	 * 
	 * @return the canvas instance.
	 */
	public <T extends PluginCanvas> T create(Plugin thiz, String identifier, short mapid, 
			CommandSender owner, String name) throws NamingOccupiedException ;
	
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
