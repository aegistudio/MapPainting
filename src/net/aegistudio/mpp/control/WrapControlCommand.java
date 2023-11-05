/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.control;

import java.util.TreeSet;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.CanvasWrapper;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class WrapControlCommand
extends ConcreteControlCommand {
    public static final String CANNOT_WRAP = "canontWrap";
    public String cannotWrap;

    public WrapControlCommand() {
        this.description = "@control.wrap.description";
        this.paramList = "<newWrapped> [<layer>]";
        this.cannotWrap = "@control.wrap.cannotWrap";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.cannotWrap = painting.getLocale(CANNOT_WRAP, this.cannotWrap, section);
    }

    @Override
    protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
        if (!sender.hasPermission("mpp.control.wrap")) {
            sender.sendMessage(painting.m_commandControlHandler.noControlPermission.replace("$canvasName", canvas.name));
            return;
        }
        int layer = 0;
        if (arguments.length < 1 || arguments.length > 2) {
            sender.sendMessage(painting.m_commandControlHandler.invalidArguments);
            return;
        }
        if (arguments.length == 2) {
            try {
                layer = Integer.parseInt(arguments[1]);
            }
            catch (Throwable throwable) {
                sender.sendMessage(painting.m_commandControlHandler.invalidFormat);
                return;
            }
        }
        if (!(canvas.canvas instanceof CanvasWrapper)) {
            sender.sendMessage(painting.m_commandControlHandler.mismatchedType.replace("$canvasName", canvas.name).replace("$canvasType", "wrapper"));
            return;
        }
        if (arguments[0].equals(canvas.name)) {
            sender.sendMessage(this.cannotWrap.replace("$canvasName", canvas.name));
            return;
        }
        MapCanvasRegistry target = painting.m_canvasManager.nameCanvasMap.get(arguments[0]);
        if (target != null && target.canvas instanceof CanvasWrapper) {
            TreeSet<String> wrapping = new TreeSet<String>();
            ((CanvasWrapper)((Object)target.canvas)).showWrapped(wrapping);
            if (wrapping.contains(canvas.name)) {
                sender.sendMessage(this.cannotWrap.replace("$canvasName", canvas.name));
                return;
            }
        }
        ((CanvasWrapper)((Object)canvas.canvas)).setWrapping(layer, arguments[0]);
        painting.ackHistory(canvas, sender);
    }
}

