/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.permissions.Permissible
 */
package net.aegistudio.mpp.canvas;

import java.util.TreeSet;
import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public class InfoCommand
extends ActualHandle {
    public static final String NOT_HOLDING = "notHolding";
    public String notHolding;
    public static final String NAME = "name";
    public String name;
    public static final String OWNER = "owner";
    public String owner;
    public static final String PAINTER = "painter";
    public String painter;
    public static final String INTERACTOR = "interactor";
    public String interactor;
    public static final String PAINTER_LISTITEM = "painterListitem";
    public String painterListitem;
    public static final String PREVILEGE = "privilege";
    public String privilege;

    public InfoCommand() {
        this.description = "@info.description";
        this.notHolding = "@info.notHolding";
        this.name = "@info.name";
        this.owner = "@info.owner";
        this.painter = "@info.painter";
        this.interactor = "@info.interactor";
        this.painterListitem = "@info.painterListItem";
        this.privilege = "@info.privilege";
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        MapCanvasRegistry registry = null;
        if (arguments.length == 0) {
            if (sender instanceof Player) {
                registry = painting.m_canvasManager.holding((Player)sender);
            }
        } else {
            registry = painting.m_canvasManager.nameCanvasMap.get(arguments[0]);
        }
        if (registry == null) {
            sender.sendMessage(this.notHolding);
            return true;
        }
        sender.sendMessage(this.name.replace("$name", registry.name).replace("$binding", registry.binding.toString()));
        sender.sendMessage(this.owner.replace("$owner", registry.owner));
        sender.sendMessage(this.painter.replace("$painterList", this.list(registry.painter)));
        sender.sendMessage(this.interactor.replace("$interactorList", this.list(registry.interactor)));
        CommandSender testing = null;
        String testingname = null;
        if (arguments.length >= 2) {
            testingname = arguments[1];
            testing = painting.getServer().getPlayer(testingname);
        } else {
            testing = sender;
            testingname = sender.getName();
        }
        TreeSet<String> previlegeList = new TreeSet<String>();
        if (registry.owner.equals(testingname)) {
            previlegeList.add(OWNER);
        }
        if (registry.select(registry.painter, testingname, (Permissible)testing)) {
            previlegeList.add(PAINTER);
        }
        if (registry.select(registry.interactor, testingname, (Permissible)testing)) {
            previlegeList.add("interact");
        }
        sender.sendMessage(this.privilege.replace("$who", testingname).replace("$privilegeList", this.list(previlegeList)));
        return true;
    }

    public String list(TreeSet<String> set) {
        StringBuilder painterList = new StringBuilder();
        boolean first = true;
        for (String painter : set) {
            if (first) {
                first = false;
            } else {
                painterList.append(", ");
            }
            painterList.append(this.painterListitem.replace("$painter", painter));
        }
        return new String(painterList);
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.notHolding = painting.getLocale(NOT_HOLDING, this.notHolding, section);
        this.name = painting.getLocale(NAME, this.name, section);
        this.owner = painting.getLocale(OWNER, this.owner, section);
        this.painter = painting.getLocale(PAINTER, this.painter, section);
        this.interactor = painting.getLocale(INTERACTOR, this.interactor, section);
        this.privilege = painting.getLocale(PREVILEGE, this.privilege, section);
        this.painterListitem = painting.getLocale(PAINTER_LISTITEM, this.painterListitem, section);
    }
}

