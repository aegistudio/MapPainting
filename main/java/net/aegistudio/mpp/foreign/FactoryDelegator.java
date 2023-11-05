/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.export.CanvasCommandHandle;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.factory.ConcreteCreateSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class FactoryDelegator<P extends Plugin, C extends PluginCanvas>
extends ConcreteCreateSubCommand
implements Delegated {
    private final P plugin;
    private final CanvasCommandHandle<P, C> handle;
    private final String factory;

    public FactoryDelegator(P plugin, CanvasCommandHandle<P, C> handle, String factory) {
        this.plugin = plugin;
        this.handle = handle;
        this.description = "[" + (Object)ChatColor.RED + plugin.getName() + (Object)ChatColor.RESET + "] " + this.handle.description();
        this.paramList = handle.paramList();
        this.factory = factory;
    }

    @Override
    public String getPlugin() {
        return this.plugin.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
        try {
            CanvasDelegator<C> delegator = (CanvasDelegator<C>)
                    painting.m_foreignCanvasManager.generate(plugin, factory, PluginCanvas.class);
            return handle.handle(plugin, sender, arguments, (C) delegator.canvas())? delegator : null;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

