package net.aegistudio.mpp.canvas;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class CanvasManager implements Module {
	/**
	 * Stores the relation between mapId / canvas and name / canvas.
	 */
	public final TreeMap<Short, MapCanvasRegistry> idCanvasMap = new TreeMap<Short, MapCanvasRegistry>();
	public final TreeMap<String, MapCanvasRegistry> nameCanvasMap = new TreeMap<String, MapCanvasRegistry>();
	public int count = 0;
	
	public void add(MapCanvasRegistry registry) {
		if(registry.removed()) return;
		idCanvasMap.put(registry.binding, registry);
		pool.remove(registry.binding);
		
		nameCanvasMap.put(registry.name, registry);
		count ++;
		
		registry.add();
	}
	
	public boolean remove(MapCanvasRegistry registry) {
		if(registry.removed()) return false;
		if(!idCanvasMap.containsKey(registry.binding)) return false;
		if(!nameCanvasMap.containsKey(registry.name)) return false;
		
		idCanvasMap.remove(registry.binding);
		pool.add(registry.binding);
		
		nameCanvasMap.remove(registry.name);
		count --;
		
		registry.remove();
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public int allocate() {
		if(pool.isEmpty()) {
			MapView view = painting.getServer().createMap(
					painting.getServer().getWorlds().get(0));
			return view != null? view.getId() : -1;
		}
		return pool.pollFirst();
	}
	
	@SuppressWarnings("deprecation")
	public MapCanvasRegistry holding(Player player) {
		ItemStack item = player.getItemInHand();
		if(item.getType() == Material.MAP)
			return idCanvasMap.get(item.getDurability());
		return this.idCanvasMap.get((short)scopeListener.parse(item));
	}
	
	@SuppressWarnings("deprecation")
	public void give(Player player, MapCanvasRegistry registry) {
		ItemStack item = player.getItemInHand();
		boolean replaceHand = false;
		if(item.getType() == Material.MAP) {
			if(item.getDurability() == registry.binding)
				replaceHand = true;
		}
		
		if(!replaceHand) {
			item = new ItemStack(Material.STONE, 1);
			scopeListener.make(item, registry);
			player.getInventory().addItem(item);
		}
		else scopeListener.make(item, registry);
	}
	
	public static final String MAP = "map";
	public CanvasColor color = new CachedCanvasColor(5);
	
	public static final String POOL = "pool";
	public final TreeSet<Short> pool = new TreeSet<Short>();
	
	public static final String SUSPECT_TIMED_OUT = "suspectTimedOut";
	public int suspectTimedOut = 40;
	
	/**
	 * Stores what the player's latest modification made on which canvas.
	 */
	public final TreeMap<String, String> latest = new TreeMap<String, String>();
	
	/**
	 * Listen for players placing / removing paintings.
	 */
	CanvasScopeListener scopeListener;
	
	public MapPainting painting;
	
	@Override
	public void load(MapPainting painting, ConfigurationSection canvas) throws Exception {
		this.painting = painting;
		
		if(canvas.contains(SUSPECT_TIMED_OUT))
			suspectTimedOut = canvas.getInt(SUSPECT_TIMED_OUT);
		else canvas.set(SUSPECT_TIMED_OUT, suspectTimedOut);
		
		scopeListener = new CanvasScopeListener(painting);
		scopeListener.load(painting, canvas);
		painting.getServer().getPluginManager().registerEvents(this.scopeListener, painting);
		
		if(!canvas.contains(MAP)) canvas.createSection(MAP);
		ConfigurationSection map = canvas.getConfigurationSection(MAP);
		
		for(String name : map.getKeys(false)) {
			ConfigurationSection canvasValue = map.getConfigurationSection(name);
			MapCanvasRegistry entry = new MapCanvasRegistry(name);
			try {
				entry.load(painting, canvasValue);
				this.add(entry);
			}
			catch(Exception e) {
				e.printStackTrace();
				pool.add(entry.binding);
			}
		}
	
		if(canvas.contains(POOL));
			pool.addAll(canvas.getShortList(POOL));
		
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
			pool.add(canvasEntry.getValue().binding);
		}
		
		canvas.set(POOL, new ArrayList<Short>(pool));
	}
}
