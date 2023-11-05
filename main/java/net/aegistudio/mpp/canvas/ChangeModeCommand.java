/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChangeModeCommand
extends ActualHandle {
    public static final String ONLY_PLAYER = "onlyPlayer";
    public String onlyPlayer;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String PAINTER_MODIFIED = "painterModified";
    public String painterModified;
    public static final String PAINTER_ADDED = "painterAdded";
    public String painterAdded;
    public static final String PAINTER_REMOVED = "painterRemoved";
    public String painterRemoved;
    public static final String INTERACTOR_ADDED = "interactorAdded";
    public String interactorAdded;
    public static final String INTERACTOR_REMOVED = "interactorRemoved";
    public String interactorRemoved;
    public static final String NOT_HOLDING = "notHolding";
    public String notHolding;
    public static final String NO_CHMOD_PERMISSION = "noChmodPermission";
    public String noChmodPermission;

    public ChangeModeCommand() {
        this.description = "@chmod.description";
        this.onlyPlayer = "@chmod.onlyPlayer";
        this.canvasNotExists = "@chmod.canvasNotExists";
        this.painterModified = "@chmod.painterModified";
        this.painterAdded = "@chmod.painterAdded";
        this.painterRemoved = "@chmod.painterRemoved";
        this.interactorAdded = "@chmod.interactorAdded";
        this.interactorRemoved = "@chmod.interactorRemoved";
        this.notHolding = "@chmod.notHolding";
        this.noChmodPermission = "@chmod.noChmodPermission";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.onlyPlayer = painting.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
        this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
        this.painterModified = painting.getLocale(PAINTER_MODIFIED, this.painterModified, section);
        this.painterAdded = painting.getLocale(PAINTER_ADDED, this.painterAdded, section);
        this.painterRemoved = painting.getLocale(PAINTER_REMOVED, this.painterRemoved, section);
        this.interactorAdded = painting.getLocale(INTERACTOR_ADDED, this.interactorAdded, section);
        this.interactorRemoved = painting.getLocale(INTERACTOR_REMOVED, this.interactorRemoved, section);
        this.notHolding = painting.getLocale(NOT_HOLDING, this.notHolding, section);
        this.noChmodPermission = painting.getLocale(NO_CHMOD_PERMISSION, this.noChmodPermission, section);
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        MapCanvasRegistry registry;
        if (arguments.length == 0) {
            sender.sendMessage(prefix + " [p|i]<+/-who>... [<name>]");
            return true;
        }
        ArrayList<String> addPainter = new ArrayList<String>();
        ArrayList<String> removePainter = new ArrayList<String>();
        ArrayList<String> addInteractor = new ArrayList<String>();
        ArrayList<String> removeInteractor = new ArrayList<String>();
        String name = null;
        for (String argument : arguments) {
            char first = argument.charAt(0);
            if (first == '+') {
                addPainter.add(argument.substring(1));
                continue;
            }
            if (first == '-') {
                removePainter.add(argument.substring(1));
                continue;
            }
            if (argument.startsWith("p+")) {
                addPainter.add(argument.substring(2));
                continue;
            }
            if (argument.startsWith("p-")) {
                removePainter.add(argument.substring(2));
                continue;
            }
            if (argument.startsWith("i+")) {
                addInteractor.add(argument.substring(2));
                continue;
            }
            if (argument.startsWith("i-")) {
                removeInteractor.add(argument.substring(2));
                continue;
            }
            name = argument;
            break;
        }
        if (name == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.onlyPlayer);
                return true;
            }
            Player player = (Player)sender;
            registry = painting.m_canvasManager.holding(player);
            if (registry == null) {
                sender.sendMessage(this.notHolding);
                return true;
            }
        } else {
            registry = painting.m_canvasManager.nameCanvasMap.get(name);
            if (registry == null) {
                sender.sendMessage(this.canvasNotExists.replace("$canvasName", name));
                return true;
            }
        }
        if (!registry.hasPermission(sender, "chmod")) {
            sender.sendMessage(this.noChmodPermission.replace("$canvasName", registry.name));
            return true;
        }
        for (String painter : addPainter) {
            if (!registry.painter.add(painter)) continue;
            this.tell(painting, painter, this.painterAdded, registry);
        }
        for (String painter : removePainter) {
            if (!registry.painter.remove(painter)) continue;
            this.tell(painting, painter, this.painterRemoved, registry);
        }
        for (String interactor : addInteractor) {
            if (!registry.interactor.add(interactor)) continue;
            this.tell(painting, interactor, this.interactorAdded, registry);
        }
        for (String interactor : removeInteractor) {
            if (!registry.interactor.remove(interactor)) continue;
            this.tell(painting, interactor, this.interactorRemoved, registry);
        }
        sender.sendMessage(this.painterModified.replace("$canvasName", registry.name));
        painting.ackHistory(registry, sender);
        return true;
    }

    public void tell(MapPainting painting, String who, String totell, MapCanvasRegistry registry) {
        Player target = painting.getServer().getPlayer(who);
        if (target != null) {
            target.sendMessage(totell.replace("$canvasName", registry.name));
        }
    }
}

