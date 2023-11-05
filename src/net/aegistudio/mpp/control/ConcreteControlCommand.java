/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp.control;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.command.CommandSender;

public abstract class ConcreteControlCommand
extends ActualHandle {
    protected String paramList = "[<parameter>]";

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        if (arguments.length < 1) {
            sender.sendMessage(prefix + " <name> " + this.paramList);
        } else {
            MapCanvasRegistry canvas = painting.getCanvas(arguments[0], sender);
            if (canvas == null) {
                sender.sendMessage(painting.m_commandControlHandler.canvasNotExists.replace("$canvasName", arguments[0]));
                return true;
            }
            String[] subArguments = new String[arguments.length - 1];
            System.arraycopy(arguments, 1, subArguments, 0, arguments.length - 1);
            this.subhandle(painting, canvas, sender, subArguments);
        }
        return true;
    }

    protected abstract void subhandle(MapPainting var1, MapCanvasRegistry var2, CommandSender var3, String[] var4);
}

