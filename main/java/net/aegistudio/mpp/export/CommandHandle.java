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

public interface CommandHandle<P extends Plugin> {
    public String description();

    public boolean handle(P var1, String var2, CommandSender var3, String[] var4);
}

