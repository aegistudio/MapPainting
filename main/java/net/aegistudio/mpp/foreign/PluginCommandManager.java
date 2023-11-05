/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import net.aegistudio.mpp.CommandHandle;
import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.export.CanvasCommandHandle;
import net.aegistudio.mpp.export.NamingException;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCommandService;
import org.bukkit.plugin.Plugin;

public class PluginCommandManager
implements PluginCommandService {
    private MapPainting mapPainting;

    public PluginCommandManager(MapPainting mapPainting) {
        this.mapPainting = mapPainting;
    }

    protected CompositeHandle findParent(String path) {
        if (path == null) {
            return null;
        }
        String[] paths = path.split("\\/");
        CompositeHandle current = this.mapPainting.m_commandHandler;
        for (int i = 0; i < paths.length - 1; ++i) {
            if (paths[i].length() == 0) continue;
            int index = current.name.indexOf(paths[i]);
            if (index < 0) {
                return null;
            }
            CommandHandle handle = current.subcommand.get(index);
            if (!(handle instanceof CompositeHandle)) {
                return null;
            }
            current = (CompositeHandle)handle;
        }
        return current;
    }

    public boolean ofPlugin(Plugin p, CompositeHandle handle, String name) {
        int index = handle.name.indexOf(name);
        if (index < 0) {
            return true;
        }
        CommandHandle cmdHandle = handle.subcommand.get(index);
        if (!(cmdHandle instanceof Delegated)) {
            return false;
        }
        Delegated delegated = (Delegated)((Object)cmdHandle);
        return delegated.getPlugin().equals(p.getName());
    }

    public <P extends Plugin> QueryResult get(P thiz, String attach) throws NamingException {
        CompositeHandle handle = this.findParent(attach);
        if (handle == null) {
            throw new NamingException("pathError", attach);
        }
        String name = attach.substring(1 + attach.lastIndexOf(47));
        if (!this.ofPlugin(thiz, handle, name)) {
            throw new NamingException("elementExisted", name);
        }
        QueryResult result = new QueryResult();
        result.handle = handle;
        result.name = name;
        return result;
    }

    @Override
    public <P extends Plugin> void register(P thiz, String attach, net.aegistudio.mpp.export.CommandHandle<P> command) throws NamingException {
        QueryResult result = this.get(thiz, attach);
        result.handle.add(result.name, new CommandDelegator<P>(thiz, command));
    }

    @Override
    public <P extends Plugin> boolean unregister(P thiz, String attach) {
        try {
            QueryResult result = this.get(thiz, attach);
            int index = result.handle.name.indexOf(result.name);
            result.handle.name.remove(index);
            result.handle.subcommand.remove(index);
            return true;
        }
        catch (NamingException e) {
            return false;
        }
    }

    @Override
    public <P extends Plugin> void registerGroup(P thiz, String attach, String description) throws NamingException {
        QueryResult result = this.get(thiz, attach);
        result.handle.add(result.name, new CompositeDelegator(thiz, description));
    }

    @Override
    public <P extends Plugin, C extends PluginCanvas> void registerCreate(P thiz, String attach, String identifier, CanvasCommandHandle<P, C> create) throws NamingException {
        QueryResult result = this.get(thiz, attach);
        result.handle.add(result.name, new FactoryDelegator<P, C>(thiz, create, identifier));
    }

    @Override
    public <P extends Plugin, C extends PluginCanvas> void registerControl(P thiz, String attach, String type, Class<? extends C> canvas, CanvasCommandHandle<P, C> control) throws NamingException {
        QueryResult result = this.get(thiz, attach);
        result.handle.add(result.name, new ControlDelegator(thiz, type, (Class<? extends PluginCanvas>)canvas, control));
    }

    class QueryResult {
        CompositeHandle handle;
        String name;

        QueryResult() {
        }
    }

}

