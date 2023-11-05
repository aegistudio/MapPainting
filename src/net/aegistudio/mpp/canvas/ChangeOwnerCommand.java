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
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ChangeOwnerCommand
extends ActualHandle
implements HazardCommand {
    public static final String ONLY_PLAYER = "onlyPlayer";
    public String onlyPlayer;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String OWNERSHIP_CHANGED = "ownershipChanged";
    public String ownershipChanged;
    public static final String OWNERSHIP_GAINED = "ownershipGained";
    public String ownershipGained;
    public static final String NOT_HOLDING = "notHolding";
    public String notHolding;
    public static final String NO_CHOWN_PERMISSION = "noChownPermission";
    public String noChownPermission;

    public ChangeOwnerCommand() {
        this.description = "@chown.description";
        this.onlyPlayer = "@chown.onlyPlayer";
        this.canvasNotExists = "@chown.canvasNotExists";
        this.ownershipChanged = "@chown.ownershipChanged";
        this.ownershipGained = "@chown.ownershipGained";
        this.notHolding = "@chown.notHolding";
        this.noChownPermission = "@chown.noChownPermission";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.onlyPlayer = painting.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
        this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
        this.ownershipChanged = painting.getLocale(OWNERSHIP_CHANGED, this.ownershipChanged, section);
        this.ownershipGained = painting.getLocale(OWNERSHIP_GAINED, this.ownershipGained, section);
        this.notHolding = painting.getLocale(NOT_HOLDING, this.notHolding, section);
        this.noChownPermission = painting.getLocale(NO_CHOWN_PERMISSION, this.noChownPermission, section);
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        MapCanvasRegistry registry;
        if (arguments.length == 0) {
            sender.sendMessage(prefix + " <receiver> [<name>]");
            return true;
        }
        if (arguments.length <= 1) {
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
            registry = painting.getCanvas(arguments[1], sender);
            if (registry == null) {
                sender.sendMessage(this.canvasNotExists.replace("$canvasName", arguments[1]));
                return true;
            }
        }
        if (!registry.hasPermission(sender, "chown")) {
            sender.sendMessage(this.noChownPermission.replace("$canvasName", registry.name));
            return true;
        }
        ChownState state = new ChownState();
        state.newOwner = arguments[0];
        state.registry = registry;
        painting.m_commandConfirmHandler.hazard(sender, this, state);
        return true;
    }

    @Override
    public void handle(MapPainting painting, CommandSender sender, Object hazardState) {
        ChownState state = (ChownState)hazardState;
        state.registry.owner = state.newOwner;
        state.registry.painter.add(state.newOwner);
        sender.sendMessage(this.ownershipChanged.replace("$canvasName", state.registry.name).replace("$newOwner", state.newOwner));
        painting.ackHistory(state.registry, sender);
        Player newOwner = painting.getServer().getPlayer(state.newOwner);
        if (newOwner != null) {
            newOwner.sendMessage(this.ownershipGained.replace("$canvasName", state.registry.name));
        }
    }

    class ChownState {
        public String newOwner;
        public MapCanvasRegistry registry;

        ChownState() {
        }
    }

}

