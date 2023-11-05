/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.canvas;

import java.io.File;
import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class PurgeCanvasCommand
extends ActualHandle {
    public static final String NO_PURGE_PERMISSION = "noPurgePermission";
    public String noPurgePermission;
    public static final String PURGE_RESULT = "purgeResult";
    public String purgeResult;
    public static final String PURGE_ON_UNLOAD = "purgeOnUnload";
    public boolean purgeOnUnload;

    public PurgeCanvasCommand() {
        this.description = "@purge.description";
        this.noPurgePermission = "@purge.noPurgePermission";
        this.purgeResult = "@purge.result";
        this.purgeOnUnload = true;
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        if (!sender.hasPermission("mpp.manager")) {
            sender.sendMessage(this.noPurgePermission);
            return true;
        }
        int purgedCount = 0;
        for (File file2 : painting.getDataFolder().listFiles(file -> file.getName().endsWith(".mpp") || file.getName().endsWith(".dat"))) {
            String filename = file2.getName();
            filename = filename.substring(filename.lastIndexOf(47) + 1);
            if (painting.m_canvasManager.nameCanvasMap.containsKey(filename = filename.substring(0, filename.lastIndexOf(46)))) continue;
            file2.delete();
            ++purgedCount;
        }
        sender.sendMessage(this.purgeResult.replace("$count", Integer.toString(purgedCount)));
        return true;
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.noPurgePermission = painting.getLocale(NO_PURGE_PERMISSION, this.noPurgePermission, section);
        this.purgeResult = painting.getLocale(PURGE_RESULT, this.purgeResult, section);
        if (section.contains(PURGE_ON_UNLOAD)) {
            this.purgeOnUnload = section.getBoolean(PURGE_ON_UNLOAD);
        } else {
            section.set(PURGE_ON_UNLOAD, (Object)this.purgeOnUnload);
        }
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
        super.save(painting, section);
        if (this.purgeOnUnload) {
            this.handle(painting, "", (CommandSender)painting.getServer().getConsoleSender(), new String[0]);
        }
    }
}

