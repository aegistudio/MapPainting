/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
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

public class DestroyCanvasCommand
extends ActualHandle
implements HazardCommand {
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String NO_PERMISSION = "noPermission";
    public String noPermission;
    public static final String UNBOUND = "unbound";
    public String unbound;
    public static final String HOLDING = "holding";
    public String holding;

    public DestroyCanvasCommand() {
        this.description = "@destroy.description";
        this.canvasNotExists = "@destroy.canvasNotExists";
        this.noPermission = "@destroy.noDestroyPermission";
        this.unbound = "@destroy.unbound";
        this.holding = "@destroy.holding";
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        if (arguments.length != 1) {
            MapCanvasRegistry canvas = null;
            if (sender instanceof Player) {
                canvas = painting.m_canvasManager.holding((Player)sender);
            }
            if (canvas != null && canvas.hasPermission(sender, "destroy")) {
                sender.sendMessage(this.holding.replace("$canvasName", canvas.name).replace("$prefix", prefix));
                painting.m_commandConfirmHandler.hazard(sender, this, canvas);
                return true;
            }
            sender.sendMessage(prefix + " <name>");
        } else {
            MapCanvasRegistry canvas = painting.getCanvas(arguments[0], sender);
            if (canvas == null) {
                sender.sendMessage(this.canvasNotExists.replace("$canvasName", arguments[0]));
                return true;
            }
            if (!canvas.hasPermission(sender, "destroy")) {
                sender.sendMessage(this.noPermission.replace("$canvasName", arguments[0]));
                return true;
            }
            painting.m_commandConfirmHandler.hazard(sender, this, canvas);
        }
        return true;
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
        this.noPermission = painting.getLocale(NO_PERMISSION, this.noPermission, section);
        this.holding = painting.getLocale(HOLDING, this.holding, section);
        this.unbound = painting.getLocale(UNBOUND, this.unbound, section);
    }

    @Override
    public void handle(MapPainting painting, CommandSender sender, Object hazardState) {
        MapCanvasRegistry canvas = (MapCanvasRegistry)hazardState;
        painting.m_canvasManager.remove(canvas);
        sender.sendMessage(this.unbound.replace("$canvasName", canvas.name));
    }
}

