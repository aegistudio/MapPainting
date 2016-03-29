package net.aegistudio.mpp.export;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Register services for a specified plugin.
 * @author aegistudio
 */

@SuppressWarnings({"unchecked", "rawtypes"})
public class DedicatedManager {
	public final Map<String, PluginCanvasFactory<?>> factory = new TreeMap<String, PluginCanvasFactory<?>>();
	public final Map<String, Set<CanvasDelegator<?>>> watchlist = new TreeMap<String, Set<CanvasDelegator<?>>>();
	
	public <T extends PluginCanvas> void register(String identifier, PluginCanvasFactory<T> factory) {	
		this.factory.put(identifier, factory);
		for(CanvasDelegator delegator : 
			Collections.synchronizedSet(this.watchlist(identifier))) {
			
			delegator.create(factory);
		}
	}
	
	protected Set<CanvasDelegator<?>> watchlist(String identifier) {
		Set<CanvasDelegator<?>> watch = this.watchlist.get(identifier);
		if(watch == null) this.watchlist.put(identifier, 
				watch = new HashSet<CanvasDelegator<?>>());
		return watch;
	}
	
	public void place(CanvasDelegator<?> delegator) {
		this.watchlist(delegator.identifier).add(delegator);
		
		PluginCanvasFactory factory = this.factory.get(delegator.identifier);
		if(factory != null) delegator.create(factory);
	}
	
	public <T extends PluginCanvas> Map<String, T> getPluginCanvases(String identifier, Class<T> canvasClazz) {
		TreeMap<String, T> canvases = new TreeMap<String, T>();
		for(CanvasDelegator delegator : 
			Collections.synchronizedSet(this.watchlist(identifier))) {
			
			if(!delegator.getRegistry().removed())
				canvases.put(delegator.getRegistry().name, (T) delegator.canvasInstance);
		}
		return canvases;
	}
}
