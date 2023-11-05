/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.export;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface CanvasCommandHandle<P extends Plugin, C extends PluginCanvas> {
    public String description();

    public String paramList();

    public boolean handle(P var1, CommandSender var2, String[] var3, C var4);
}

