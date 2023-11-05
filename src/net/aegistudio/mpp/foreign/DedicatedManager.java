/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.foreign;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.aegistudio.mpp.export.NamingException;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCanvasFactory;
import net.aegistudio.mpp.export.PluginCanvasRegistry;

public class DedicatedManager {
    public final Map<String, PluginCanvasFactory<?>> factory = new TreeMap();
    public final Map<String, Set<CanvasDelegator<?>>> watchlist = new TreeMap();

    public <T extends PluginCanvas> void register(String identifier, PluginCanvasFactory<T> factory) throws NamingException {
        if (this.factory.containsKey(identifier) && this.factory.get(identifier).getClass() != factory.getClass()) {
            throw new NamingException("factoryIdentifier", identifier);
        }
        this.factory.put(identifier, factory);
        for (CanvasDelegator<?> delegator : Collections.synchronizedSet(this.watchlist(identifier))) {
            this.create(delegator, factory);
        }
    }

    public void reset() {
        this.watchlist.clear();
    }

    protected void create(CanvasDelegator delegator, PluginCanvasFactory factory) {
        if (delegator.factory == factory) {
            return;
        }
        delegator.create(factory);
    }

    protected Set<CanvasDelegator<?>> watchlist(String identifier) {
        Set<CanvasDelegator<?>> watch = this.watchlist.get(identifier);
        if (watch == null) {
            watch = new HashSet();
            this.watchlist.put(identifier, watch);
        }
        return watch;
    }

    public void place(CanvasDelegator<?> delegator) {
        this.watchlist(delegator.identifier).add(delegator);
        PluginCanvasFactory<?> factory = this.factory.get(delegator.identifier);
        if (factory != null) {
            this.create(delegator, factory);
        }
    }

    public <T extends PluginCanvas> Collection<PluginCanvasRegistry<T>> getPluginCanvases(String identifier, Class<T> canvasClazz) {
        HashSet returnValue = new HashSet<PluginCanvasRegistry<T>>();
        returnValue.addAll(this.watchlist(identifier));
        return returnValue;
    }
}

