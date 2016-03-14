package net.aegistudio.mpp.canvas;

import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class CanvasManager implements Module {
	public final TreeMap<Short, MapCanvasRegistry> idCanvasMap = new TreeMap<Short, MapCanvasRegistry>();
	public final TreeMap<String, MapCanvasRegistry> nameCanvasMap = new TreeMap<String, MapCanvasRegistry>();
	
	public static final String MAP = "map";
	
	public CanvasColor color = new CachedCanvasColor(5);
	
	@Override
	public void load(MapPainting painting, ConfigurationSection canvas) throws Exception {
		if(!canvas.contains(MAP)) canvas.createSection(MAP);
		ConfigurationSection map = canvas.getConfigurationSection(MAP);
		
		for(String name : map.getKeys(false)) try {
			ConfigurationSection canvasValue = map.getConfigurationSection(name);
			MapCanvasRegistry entry = new MapCanvasRegistry(name);
			entry.load(painting, canvasValue);
			
			idCanvasMap.put(entry.binding, entry);
			nameCanvasMap.put(entry.name, entry);
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
