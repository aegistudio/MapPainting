/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp.control;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.color.PseudoColor;
import net.aegistudio.mpp.tool.PixelTapMemento;
import org.bukkit.command.CommandSender;

public class TapControlCommand
extends ConcreteControlCommand {
    public TapControlCommand() {
        this.description = "@control.tap.description";
        this.paramList = "<x> <y> <color>";
    }

    @Override
    protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
        int x;
        PseudoColor color;
        int y;
        if (!sender.hasPermission("mpp.control.tap")) {
            sender.sendMessage(painting.m_commandControlHandler.noControlPermission.replace("$canvasName", canvas.name));
            return;
        }
        if (arguments.length != 3) {
            sender.sendMessage(painting.m_commandControlHandler.invalidArguments);
            return;
        }
        try {
            x = Integer.parseInt(arguments[0]);
            y = Integer.parseInt(arguments[1]);
            color = painting.m_colorManager.parseColor(arguments[2]);
        }
        catch (Throwable t) {
            sender.sendMessage(painting.m_commandControlHandler.invalidFormat);
            return;
        }
        Interaction interaction = new Interaction(x, y, sender, null, null, false, null);
        canvas.history.add(new PixelTapMemento(canvas.canvas, interaction, color.color, this.paramList));
        painting.ackHistory(canvas, sender);
    }
}

