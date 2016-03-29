package net.aegistudio.mpp.export;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

/**
 * This is a plugin canvas service provider. You
 * could register plugin canvas class, their factory
 * with this service.
 * 
 * @author aegistudio
 */

public final class PluginCanvasManager implements PluginCanvasService{
	public final TreeMap<String, DedicatedManager> pluginMap = new TreeMap<String, DedicatedManager>();
	
	final MapPainting mapPainting;
	public PluginCanvasManager(MapPainting mapPainting) {
		this.mapPainting = mapPainting;
	}

	protected DedicatedManager plugin(Plugin plugin) {
		DedicatedManager manager = pluginMap.get(plugin.getName());
		if(manager == null) pluginMap.put(plugin.getName(), manager = new DedicatedManager());
		return manager;
	}

	@Override
	public <T extends PluginCanvas> void register(Plugin thiz, String identifier, 
			PluginCanvasFactory<T> factory) throws NamingOccupiedException {
		
		this.plugin(thiz).register(identifier, factory);
	}

	@Override
	public <T extends PluginCanvas> Map<String, T> getPluginCanvases(Plugin thiz, String identifier,
			Class<T> canvasClazz) {
		
		return this.plugin(thiz).getPluginCanvases(identifier, canvasClazz);
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized <T extends PluginCanvas> void create(Plugin thiz, String identifier, short mapid, CommandSender owner,
			String name, T canvas) throws NamingOccupiedException {
		synchronized(this) {
			if(mapPainting.canvas.idCanvasMap.containsKey(mapid)) throw new NamingOccupiedException("mapid", mapid);
			if(mapPainting.canvas.nameCanvasMap.containsKey(name)) throw new NamingOccupiedException("name", name);
			
			MapCanvasRegistry registry = new MapCanvasRegistry(name);
			registry.binding = mapid;
			registry.owner = owner == null? "" : owner.getName();
			registry.view = mapPainting.getServer().getMap(mapid);
			
			CanvasDelegator<T> delegator = new CanvasDelegator<T>(mapPainting);
			registry.canvas = delegator;
			this.plugin(thiz).place(delegator);
			
			mapPainting.canvas.add(registry);
		}
	}

	@Override
	public boolean destroy(Plugin thiz, String identifier, String name) {
		MapCanvasRegistry registry = mapPainting.canvas.nameCanvasMap.get(name);
		if(registry == null) return false;
		if(!(registry.canvas instanceof CanvasDelegator)) return false;
		CanvasDelegator<?> delegator = (CanvasDelegator<?>) registry.canvas;
		if(!delegator.plugin.equals(thiz.getName())) return false;
		if(!delegator.identifier.equals(identifier)) return false;
		
		this.plugin(thiz).watchlist(identifier).remove(delegator);
		return mapPainting.canvas.remove(registry);
	}
}
