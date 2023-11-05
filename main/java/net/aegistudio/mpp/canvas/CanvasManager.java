
package net.aegistudio.mpp.canvas;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

public class CanvasManager
implements Module {
    public final TreeMap<Integer, MapCanvasRegistry> idCanvasMap = new TreeMap<Integer, MapCanvasRegistry>();
    public final TreeMap<String, MapCanvasRegistry> nameCanvasMap = new TreeMap<String, MapCanvasRegistry>();
    public int count = 0;
    public static final String MAP = "map";
    public CanvasColor color = new CachedCanvasColor(5);
    public static final String POOL = "pool";
    public final TreeSet<Integer> pool = new TreeSet();
    public static final String SUSPECT_TIMED_OUT = "suspectTimedOut";
    public int suspectTimedOut = 40;
    public final TreeMap<String, String> latest = new TreeMap();
    public CanvasScopeListener scopeListener;
    public MapPainting painting;

    public void add(MapCanvasRegistry registry) {
        if (registry.removed()) {
            return;
        }
        this.idCanvasMap.put( (int) registry.binding, registry);
        this.pool.remove(registry.binding);
        this.nameCanvasMap.put(registry.name, registry);
        ++this.count;
        registry.add();
    }

    public boolean remove(MapCanvasRegistry registry) {
        if (registry.removed()) {
            return false;
        }
        if (!this.idCanvasMap.containsKey(registry.binding)) {
            return false;
        }
        if (!this.nameCanvasMap.containsKey(registry.name)) {
            return false;
        }
        this.idCanvasMap.remove(registry.binding);
        this.pool.add(registry.binding);
        this.nameCanvasMap.remove(registry.name);
        --this.count;
        registry.remove();
        return true;
    }

    public MapCanvasRegistry holding(Player player) {
        ItemStack item = player.getItemInHand();
        if (item.getType() == Material.FILLED_MAP) {
            return this.idCanvasMap.get((int) ((MapMeta) item.getItemMeta()).getMapId());
        }
        return this.idCanvasMap.get((int)this.scopeListener.parse(item));
    }

    public void give(Player player, MapCanvasRegistry registry, int quantity) {
        ItemStack item = player.getItemInHand();
        boolean replaceHand = false;
        
        // this section flags replacing a map in hand with the new item
        
        if (item.getType() == Material.FILLED_MAP && (int) ((MapMeta) item.getItemMeta()).getMapId() == registry.binding) {
            replaceHand = true;
        }
        if (!replaceHand) {
            item = new ItemStack(Material.STONE, quantity);
            this.scopeListener.make(item, registry);
            player.getInventory().addItem(item);
        } else {
            this.scopeListener.make(item, registry);
        }
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection canvas) throws Exception {
        this.painting = painting;
        if (canvas.contains(SUSPECT_TIMED_OUT)) {
            this.suspectTimedOut = canvas.getInt(SUSPECT_TIMED_OUT);
        } else {
            canvas.set(SUSPECT_TIMED_OUT, (Object)this.suspectTimedOut);
        }
        this.scopeListener = new CanvasScopeListener(painting);
        this.scopeListener.load(painting, canvas);
        painting.getServer().getPluginManager().registerEvents((Listener)this.scopeListener, (Plugin)painting);
        if (!canvas.contains(MAP)) {
            canvas.createSection(MAP);
        }
        ConfigurationSection map = canvas.getConfigurationSection(MAP);
        for (String name : map.getKeys(false)) {
            ConfigurationSection canvasValue = map.getConfigurationSection(name);
            MapCanvasRegistry entry = new MapCanvasRegistry(name);
            try {
                entry.load(painting, canvasValue);
                this.add(entry);
            }
            catch (Exception e) {
                e.printStackTrace();
                this.pool.add(entry.binding);
            }
        }
        if (canvas.contains(POOL)) {
            // empty if block
        }
        this.pool.addAll(canvas.getIntegerList(POOL));
        painting.getServer().getPluginManager().registerEvents((Listener)new CanvasPaintListener(painting), (Plugin)painting);
        painting.getServer().getPluginManager().registerEvents((Listener)new CanvasSwitchListener(painting), (Plugin)painting);
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection canvas) throws Exception {
        if (!canvas.contains(MAP)) {
            canvas.createSection(MAP);
        }
        ConfigurationSection map = canvas.getConfigurationSection(MAP);
        for (Map.Entry<String, MapCanvasRegistry> canvasEntry : this.nameCanvasMap.entrySet()) {
            try {
                if (!map.contains(canvasEntry.getKey())) {
                    map.createSection(canvasEntry.getKey());
                }
                canvasEntry.getValue().save(painting, map.getConfigurationSection(canvasEntry.getKey()));
            }
            catch (Exception e) {
                e.printStackTrace();
                this.pool.add(canvasEntry.getValue().binding);
            }
        }
        canvas.set(POOL, new ArrayList<Integer>(this.pool));
    }

	public int allocate() {
        if (this.pool.isEmpty()) {
            MapView view = this.painting.getServer().createMap((World)this.painting.getServer().getWorlds().get(0));
            
            return view != null ? (int)view.getId() : -1;
        }
        return this.pool.pollFirst();
    }
}



