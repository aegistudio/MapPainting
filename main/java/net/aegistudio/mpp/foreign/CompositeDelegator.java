/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import net.aegistudio.mpp.CompositeHandle;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class CompositeDelegator
extends CompositeHandle
implements Delegated {
    public Plugin plugin;

    public CompositeDelegator(Plugin plugin, String description) {
        this.plugin = plugin;
        this.description = "[" + (Object)ChatColor.RED + plugin.getName() + (Object)ChatColor.RESET + "] " + description;
    }

    @Override
    public String getPlugin() {
        return this.plugin.getName();
    }
}

