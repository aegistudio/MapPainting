package net.aegistudio.mpp.canvas;

import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class CanvasManager implements Module {
	/**
	 * Stores the relation between mapId / canvas and  name / canvas.
	 */
	public final TreeMap<Short, MapCanvasRegistry> idCanvasMap = new TreeMap<Short, MapCanvasRegistry>();
	public final TreeMap<String, MapCanvasRegistry> nameCanvasMap = new TreeMap<String, MapCanvasRegistry>();
	public int count = 0;
	
	public void add(MapCanvasRegistry registry) {
		if(registry.removed()) return;
		idCanvasMap.put(registry.binding, registry);
		nameCanvasMap.put(registry.name, registry);
		count ++;
		
		registry.add();
	}
	
	public boolean remove(MapCanvasRegistry registry) {
		if(registry.removed()) return false;
		if(!idCanvasMap.containsKey(registry.binding)) return false;
		if(!nameCanvasMap.containsKey(registry.name)) return false;
		
		idCanvasMap.remove(registry.binding);
		nameCanvasMap.remove(registry.name);
		count --;
		
		registry.remove();
		return true;
	}
	
	public static final String MAP = "map";
	public CanvasColor color = new CachedCanvasColor(5);
	
	public static final String SUSPECT_TIMED_OUT = "suspectTimedOut";
	public int suspectTimedOut = 40;
	
	/**
	 * Stores what the player's latest modification made on which canvas.
	 */
	public final TreeMap<String, String> latest = new TreeMap<String, String>();
	
	@Override
	public void load(MapPainting painting, ConfigurationSection canvas) throws Exception {
		if(canvas.contains(SUSPECT_TIMED_OUT))
			suspectTimedOut = canvas.getInt(SUSPECT_TIMED_OUT);
		else canvas.set(SUSPECT_TIMED_OUT, suspectTimedOut);
		
		if(!canvas.contains(MAP)) canvas.createSection(MAP);
		ConfigurationSection map = canvas.getConfigurationSection(MAP);
		
		for(String name : map.getKeys(false)) try {
			ConfigurationSection canvasValue = map.getConfigurationSection(name);
			MapCanvasRegistry entry = new MapCanvasRegistry(name);
			entry.load(painting, canvasValue);
			
			this.add(entry);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		painting.getServer().getPluginManager().registerEvents(new CanvasPaintListener(painting), painting);
	}
	
	@Override
	public void save(MapPainting painting, ConfigurationSection canvas) throws Exception {
		if(!canvas.contains(MAP)) canvas.createSection(MAP);
		ConfigurationSection map = canvas.getConfigurationSection(MAP);
		
		for(Entry<String, MapCanvasRegistry> canvasEntry : nameCanvasMap.entrySet()) try{
			if(!map.contains(canvasEntry.getKey()))
				map.createSection(canvasEntry.getKey());
			canvasEntry.getValue().save(painting,
					map.getConfigurationSection(canvasEntry.getKey()));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
