/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.export;

import org.bukkit.plugin.Plugin;

public interface PluginCommandService {
    public <P extends Plugin> void register(P var1, String var2, CommandHandle<P> var3) throws NamingException;

    public <P extends Plugin> boolean unregister(P var1, String var2);

    public <P extends Plugin> void registerGroup(P var1, String var2, String var3) throws NamingException;

    public <P extends Plugin, C extends PluginCanvas> void registerCreate(P var1, String var2, String var3, CanvasCommandHandle<P, C> var4) throws NamingException;

    public <P extends Plugin, C extends PluginCanvas> void registerControl(P var1, String var2, String var3, Class<? extends C> var4, CanvasCommandHandle<P, C> var5) throws NamingException;
}

