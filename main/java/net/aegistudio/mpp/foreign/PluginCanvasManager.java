/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.map.MapView
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import java.util.Collection;
import java.util.TreeMap;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.export.NamingException;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCanvasFactory;
import net.aegistudio.mpp.export.PluginCanvasRegistry;
import net.aegistudio.mpp.export.PluginCanvasService;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class PluginCanvasManager
implements PluginCanvasService {
    public final TreeMap<String, DedicatedManager> pluginMap = new TreeMap();
    final MapPainting mapPainting;

    public PluginCanvasManager(MapPainting mapPainting) {
        this.mapPainting = mapPainting;
    }

    protected DedicatedManager plugin(String plugin) {
        DedicatedManager manager = this.pluginMap.get(plugin);
        if (manager == null) {
            manager = new DedicatedManager();
            this.pluginMap.put(plugin, manager);
        }
        return manager;
    }

    protected DedicatedManager plugin(Plugin plugin) {
        return this.plugin(plugin.getName());
    }

    @Override
    public <T extends PluginCanvas> void register(Plugin thiz, String identifier, PluginCanvasFactory<T> factory) throws NamingException {
        this.plugin(thiz).register(identifier, factory);
    }

    @Override
    public <T extends PluginCanvas> Collection<PluginCanvasRegistry<T>> getPluginCanvases(Plugin thiz, String identifier, Class<T> canvasClazz) {
        return this.plugin(thiz).getPluginCanvases(identifier, canvasClazz);
    }

    @Override
    public <T extends PluginCanvas> PluginCanvasRegistry<T> generate(Plugin thiz, String identifier, Class<T> canvasClazz) throws NamingException {
        PluginCanvasFactory<?> factory = this.plugin((Plugin)thiz).factory.get(identifier);
        if (factory == null) {
            throw new NamingException("missingFactory", thiz.getName().concat(".").concat(identifier));
        }
        CanvasDelegator delegator = new CanvasDelegator(this.mapPainting);
        delegator.plugin = thiz.getName();
        delegator.identifier = identifier;
        delegator.factory = factory;
        delegator.canvasInstance = delegator.factory.create(delegator);
        return delegator;
    }

    @Override
    public <T extends PluginCanvas> void create(int mapid, CommandSender owner, String name, PluginCanvasRegistry<T> pluginCanvasRegistry) throws NamingException {
        if (this.mapPainting.m_canvasManager.idCanvasMap.containsKey(mapid)) {
            throw new NamingException("mapid", mapid);
        }
        if (this.mapPainting.m_canvasManager.nameCanvasMap.containsKey(name)) {
            throw new NamingException("name", name);
        }
        MapCanvasRegistry registry = new MapCanvasRegistry(name);
        registry.canvas = (CanvasDelegator)pluginCanvasRegistry;
        registry.binding = (int) mapid;
        registry.owner = owner == null ? "" : owner.getName();
        registry.view = this.mapPainting.getServer().getMap(mapid);
        this.mapPainting.m_canvasManager.add(registry);
    }

    @Override
    public <T extends PluginCanvas> void create(CommandSender owner, String name, PluginCanvasRegistry<T> pluginCanvasRegistry) throws NamingException {
        if (this.mapPainting.m_canvasManager.nameCanvasMap.containsKey(name)) {
            throw new NamingException("name", name);
        }
        int stored = this.mapPainting.m_canvasManager.allocate();
        if (stored < 0) {
            throw new NamingException("mapid", stored);
        }
        MapCanvasRegistry registry = new MapCanvasRegistry(name);
        registry.canvas = (CanvasDelegator)pluginCanvasRegistry;
        registry.binding = (int) stored;
        registry.owner = owner == null ? "" : owner.getName();
        registry.view = this.mapPainting.getServer().getMap(registry.binding);
        this.mapPainting.m_canvasManager.add(registry);
    }

    @Override
    public <T extends PluginCanvas> boolean destroy(PluginCanvasRegistry<T> registry) {
        if (registry == null) {
            return false;
        }
        if (!(registry instanceof CanvasDelegator)) {
            return false;
        }
        CanvasDelegator delegator = (CanvasDelegator)registry;
        MapCanvasRegistry canvasRegistry = delegator.getRegistry();
        if (canvasRegistry == null) {
            return false;
        }
        if (canvasRegistry.removed()) {
            return false;
        }
        return this.mapPainting.m_canvasManager.remove(canvasRegistry);
    }

    public void reset() {
        for (DedicatedManager m : this.pluginMap.values()) {
            m.reset();
        }
    }

    protected <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin thiz, String identifier, MapCanvasRegistry registry, Class<T> clazz) {
        if (registry == null) {
            return null;
        }
        if (!(registry.canvas instanceof CanvasDelegator)) {
            return null;
        }
        CanvasDelegator delegator = (CanvasDelegator)registry.canvas;
        if (delegator.plugin().equals(thiz.getName()) && delegator.identifier().equals(identifier)) {
            return delegator;
        }
        return null;
    }

    @Override
    public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin thiz, String identifier, String name, Class<T> clazz) {
        return this.get(thiz, identifier, this.mapPainting.m_canvasManager.nameCanvasMap.get(name), clazz);
    }

    @Override
    public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin thiz, String identifier, int mapid, Class<T> clazz) {
        return this.get(thiz, identifier, this.mapPainting.m_canvasManager.idCanvasMap.get(mapid), clazz);
    }

    @Override
    public boolean has(String name) {
        return this.mapPainting.m_canvasManager.nameCanvasMap.containsKey(name);
    }

    @Override
    public boolean has(int mapid) {
        return this.mapPainting.m_canvasManager.idCanvasMap.containsKey(mapid);
    }

    @Override
    public <T extends PluginCanvas> void place(Location block, BlockFace blockFace, PluginCanvasRegistry<T> registry) throws NamingException {
        if (blockFace.equals((Object)BlockFace.UP) || blockFace.equals((Object)BlockFace.DOWN)) {
            throw new NamingException("blockFace", (Object)blockFace);
        }
        this.mapPainting.m_canvasManager.scopeListener.placeFrame(block, blockFace, ((CanvasDelegator)registry).getRegistry());
    }
}

