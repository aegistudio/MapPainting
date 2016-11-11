package net.aegistudio.mpp.export;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * <p>Provides services for registering, creating, destroying and maintaining canvases from other plugins.</p>
 * 
 * <p>When you want to register a plugin canvas (say <code>MyCanvas</code>), with canvas factory (say <code>MyFactory</code>) to
 * Map Painting, you should retrieve the plugin canvas service first, and then register it.</p>
 * 
 * <p><b><code style="font-family: Courier New;">
 * &nbsp;&nbsp;//Retrieve plugin canvas service provider first.<br>
 * &nbsp;&nbsp;PluginCanvasService service = plugin.getServer().getServicesManager()<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.getRegistration(PluginCanvasService.class).getProvider();<br>
 * <br>
 * &nbsp;&nbsp;//Create and configure the factory then.<br> 
 * &nbsp;&nbsp;PluginCanvasFactory&lt;MyCanvas&gt; factory = new MyFactory();<br>
 * &nbsp;&nbsp;...//Some configuration for factory.<br>
 * <br>
 * &nbsp;&nbsp;//Register factory finally.<br> 
 * &nbsp;&nbsp;service.register(plugin, "myfactory", factory);<br>
 * </code></b></p>
 * 
 * <p>If you successfully register your canvas factory, and you want to create a canvas of <code>MyCanvas</code>
 * in some case (like a player issue create canvas command), you should call the generate first, then create.</p>
 * 
 * <p><b><code style="font-family: Courier New;">
 * &nbsp;&nbsp;//Generate the canvas.<br>
 * &nbsp;&nbsp;PluginCanvasRegistry&lt;MyCanvas&gt; registry = service<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.generate(plugin, "myfactory", MyCanvas.class);<br>
 * <br>
 * &nbsp;&nbsp;//Do some initial work for MyCanvas.<br> 
 * &nbsp;&nbsp;MyCanvas canvas = registry.canvas();<br>
 * &nbsp;&nbsp;...//Some initial work.<br>
 * <br>
 * &nbsp;&nbsp;//Create the canvas, assume a player create the canvas.<br> 
 * &nbsp;&nbsp;short mapid = allocateNextMapId();<br>
 * &nbsp;&nbsp;Player player = getWhoCreateCanvas();<br>
 * &nbsp;&nbsp;String name = getCanvasNameToCreate();<br>
 * &nbsp;&nbsp;service.create(mapid, player, name, registry);<br>
 * </code></b></p>
 * 
 * <p>There're other operations for plugin canvases, read following javadocs for detailed information.</p>
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
	public <T extends PluginCanvas> void register(Plugin thiz, String identifier, PluginCanvasFactory<T> factory) throws NamingException;
	
	/**
	 * Get all canvases that belongs to the same identifier.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @return the canvas name to the canvas instance.
	 */
	public <T extends PluginCanvas> Collection<PluginCanvasRegistry<T>> getPluginCanvases(Plugin thiz, 
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
	 * Bind the generated plugin canvas registry to a map id and name.
	 * @param mapid the map for creating the canvas.
	 * @param owner the owner of the canvas. only this plugin could manipulate it when set to null.
	 * @param name the name of the canvas.
	 * @param registry a suitable creation of the canvas.
	 * @throws NamingException thrown when the canvas is occupied.
	 * 
	 * @see net.aegistudio.mpp.export.PluginCanvasService#generate
	 */
	public <T extends PluginCanvas> void create(short mapid, CommandSender owner, 
			String name, PluginCanvasRegistry<T> registry) throws NamingException ;
	
	/**
	 * Bind the generated plugin canvas registry to the name, mapid is picked from pool or auto-generated
	 * if there's no redundant one.
	 * 
	 * @param owner the owner of the canvas. only this plugin could manipulate it when set to null.
	 * @param name the name of the canvas.
	 * @param registry a suitable creation of the canvas.
	 * @throws NamingException thrown when the canvas is occupied.
	 * 
	 * @see net.aegistudio.mpp.export.PluginCanvasService#generate
	 */
	public <T extends PluginCanvas> void create(CommandSender owner, String name, 
			PluginCanvasRegistry<T> registry) throws NamingException;
	
	/**
	 * Place a canvas to a face of the block.
	 * @param block the location of block
	 * @param blockFace which face will the canvas be placed
	 * @param registry the registry
	 * @throws NamingException thrown when face incorrect, or canvas improper.
	 */
	public <T extends PluginCanvas> void place(Location block, BlockFace blockFace, 
			PluginCanvasRegistry<T> registry) throws NamingException;
	
	/**
	 * Unbind the registry from its bound mapid and name.
	 * @param registry the registry to remove.
	 * @return whether the removal is success.
	 */
	public <T extends PluginCanvas> boolean destroy(PluginCanvasRegistry<T> registry);
	
	/**
	 * Check the existence of a canvas name.
	 * @param name the canvas name
	 * @return is the canvas already existsted.
	 */
	public boolean has(String name);
	
	/**
	 * Check the existence of a map id.
	 * @param mapid the mapid.
	 * @return is the canvas already existed.
	 */
	public boolean has(short mapid);
	
	/**
	 * Retrieve the corresponding registry with specified name.
	 * @param thiz the caller plugin
	 * @param identifier the identifier of the canvas factory.
	 * @param name the name of the canvas.
	 * @param clazz the canvas class that is assumed.
	 * @return the corresponding plugin canvas registry.
	 */
	public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin thiz, String identifier, String name, Class<T> clazz);

	/**
	 * Retrieve the corresponding registry with specified map id.
	 * @param thiz the caller plugin.
	 * @param identifier the identifier of the canvas factory.
	 * @param mapid the id of the map.
	 * @param clazz the canvas class that is assumed.
	 * @return the corresponding plugin canvas registry.
	 */
	public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin thiz, String identifier, short mapid, Class<T> clazz);
}
