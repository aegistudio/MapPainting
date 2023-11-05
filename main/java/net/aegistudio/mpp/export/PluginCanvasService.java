/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.export;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface PluginCanvasService {
    public <T extends PluginCanvas> void register(Plugin var1, String var2, PluginCanvasFactory<T> var3) throws NamingException;

    public <T extends PluginCanvas> Collection<PluginCanvasRegistry<T>> getPluginCanvases(Plugin var1, String var2, Class<T> var3);

    public <T extends PluginCanvas> PluginCanvasRegistry<T> generate(Plugin var1, String var2, Class<T> var3) throws NamingException;

    public <T extends PluginCanvas> void create(int var1, CommandSender var2, String var3, PluginCanvasRegistry<T> var4) throws NamingException;

    public <T extends PluginCanvas> void create(CommandSender var1, String var2, PluginCanvasRegistry<T> var3) throws NamingException;

    public <T extends PluginCanvas> void place(Location var1, BlockFace var2, PluginCanvasRegistry<T> var3) throws NamingException;

    public <T extends PluginCanvas> boolean destroy(PluginCanvasRegistry<T> var1);

    public boolean has(String var1);

    public boolean has(int var1);

    public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin var1, String var2, String var3, Class<T> var4);

    public <T extends PluginCanvas> PluginCanvasRegistry<T> get(Plugin var1, String var2, int var3, Class<T> var4);
}

