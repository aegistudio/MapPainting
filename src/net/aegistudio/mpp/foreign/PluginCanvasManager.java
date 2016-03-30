package net.aegistudio.mpp.foreign;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.export.NamingException;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCanvasFactory;
import net.aegistudio.mpp.export.PluginCanvasRegistry;
import net.aegistudio.mpp.export.PluginCanvasService;

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

	protected DedicatedManager plugin(String plugin) {
		DedicatedManager manager = pluginMap.get(plugin);
		if(manager == null) pluginMap.put(plugin, manager = new DedicatedManager());
		return manager;
	}
	
	protected DedicatedManager plugin(Plugin plugin) {
		return this.plugin(plugin.getName());
	}

	@Override
	public <T extends PluginCanvas> void register(Plugin thiz, String identifier, 
			PluginCanvasFactory<T> factory) {
		
		this.plugin(thiz).register(identifier, factory);
	}

	@Override
	public <T extends PluginCanvas> Map<String, PluginCanvasRegistry<T>> 
		getPluginCanvases(Plugin thiz, String identifier, Class<T> canvasClazz) {
		
		return this.plugin(thiz).getPluginCanvases(identifier, canvasClazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PluginCanvas> PluginCanvasRegistry<T> generate(Plugin thiz, String identifier,
			Class<T> canvasClazz) throws NamingException {
		PluginCanvasFactory<T> factory = (PluginCanvasFactory<T>) this.plugin(thiz).factory.get(identifier);
		if(factory == null) throw new NamingException("missingFactory", 
				thiz.getName().concat(".").concat(identifier));
		
		CanvasDelegator<T> delegator = new CanvasDelegator<T>(mapPainting);
		delegator.plugin = thiz.getName();
		delegator.identifier = identifier;
		delegator.factory = factory;
		delegator.canvasInstance = delegator.factory.create(delegator);
		return delegator;
	}

	@Override
	public boolean destroy(Plugin thiz, String identifier, String name) {
		MapCanvasRegistry registry = mapPainting.canvas.nameCanvasMap.get(name);
		if(registry == null) return false;
		if(!(registry.canvas instanceof CanvasDelegator)) return false;
		CanvasDelegator<?> delegator = (CanvasDelegator<?>) registry.canvas;
		if(!delegator.plugin.equals(thiz.getName())) return false;
		if(!delegator.identifier.equals(identifier)) return false;
		
		return mapPainting.canvas.remove(registry);
	}

	@SuppressWarnings("deprecation")
	@Override
	public <T extends PluginCanvas> void create(short mapid, CommandSender owner, String name,
			PluginCanvasRegistry<T> pluginCanvasRegistry) throws NamingException {
		if(mapPainting.canvas.idCanvasMap.containsKey(mapid)) throw new NamingException("mapid", mapid);
		if(mapPainting.canvas.nameCanvasMap.containsKey(name)) throw new NamingException("name", name);
		
		MapCanvasRegistry registry = new MapCanvasRegistry(name);
		registry.canvas = (CanvasDelegator<T>)pluginCanvasRegistry;
		registry.binding = mapid;
		registry.owner = owner == null? "" : owner.getName();
		registry.view = mapPainting.getServer().getMap(mapid);
		
		mapPainting.canvas.add(registry);
	}
	
	public void reset() {
		for(DedicatedManager m : this.pluginMap.values()) m.reset();
	}
}
