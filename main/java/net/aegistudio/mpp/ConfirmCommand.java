/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp;

import java.util.HashMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class ConfirmCommand
extends ActualHandle {
    public HashMap<CommandSender, Object> status;
    public HashMap<CommandSender, HazardCommand> command;
    public static final String NOTHING_TO_CONFIRM = "nothingToConfirm";
    public String nothingToConfirm;
    public static final String PLEASE_CONFIRM = "pleaseConfirm";
    public String pleaseConfirm;

    public ConfirmCommand() {
        this.description = "@confirm.description";
        this.status = new HashMap();
        this.command = new HashMap();
        this.nothingToConfirm = "@confirm.nothingToConfirm";
        this.pleaseConfirm = "@confirm.pleaseConfirm";
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        HazardCommand command = this.command.remove((Object)sender);
        Object status = this.status.remove((Object)sender);
        if (command == null) {
            sender.sendMessage(this.nothingToConfirm);
        } else {
            command.handle(painting, sender, status);
        }
        return true;
    }

    public void remove(CommandSender sender) {
        this.status.remove((Object)sender);
        this.command.remove((Object)sender);
    }

    public void hazard(CommandSender sender, HazardCommand command, Object status) {
        sender.sendMessage(this.pleaseConfirm);
        this.status.put(sender, status);
        this.command.put(sender, command);
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.nothingToConfirm = painting.getLocale(NOTHING_TO_CONFIRM, this.nothingToConfirm, section);
        this.pleaseConfirm = painting.getLocale(PLEASE_CONFIRM, this.pleaseConfirm, section);
    }
}

