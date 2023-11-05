/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.export.CommandHandle;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandDelegator<P extends Plugin>
extends ActualHandle
implements Delegated {
    private final P plugin;
    private final CommandHandle<P> handle;

    public CommandDelegator(P plugin, CommandHandle<P> handle) {
        this.plugin = plugin;
        this.handle = handle;
        this.description = "[" + (Object)ChatColor.RED + plugin.getName() + (Object)ChatColor.RESET + "] " + this.handle.description();
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        return this.handle.handle(this.plugin, prefix, sender, arguments);
    }

    @Override
    public String getPlugin() {
        return this.plugin.getName();
    }
}

