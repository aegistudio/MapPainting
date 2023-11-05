/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package net.aegistudio.mpp.foreign;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.control.ConcreteControlCommand;
import net.aegistudio.mpp.export.CanvasCommandHandle;
import net.aegistudio.mpp.export.PluginCanvas;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ControlDelegator
extends ConcreteControlCommand {
    private final Plugin plugin;
    private final Class<? extends PluginCanvas> pluginCanvas;
    private final String type;
    private final CanvasCommandHandle handle;

    public ControlDelegator(Plugin plugin, String type, Class<? extends PluginCanvas> pluginCanvas, CanvasCommandHandle handle) {
        this.plugin = plugin;
        this.type = type;
        this.pluginCanvas = pluginCanvas;
        this.handle = handle;
        this.description = "[" + (Object)ChatColor.RED + plugin.getName() + (Object)ChatColor.RESET + "] " + this.handle.description();
        this.paramList = this.handle.paramList();
    }

    @Override
    protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
        String typeError = painting.m_commandControlHandler.mismatchedType.replace("$canvasName", canvas.name).replace("$canvasType", this.type);
        if (!(canvas.canvas instanceof CanvasDelegator)) {
            sender.sendMessage(typeError);
            return;
        }
        CanvasDelegator delegator = (CanvasDelegator)canvas.canvas;
        if (!this.pluginCanvas.isAssignableFrom(delegator.canvas().getClass())) {
            sender.sendMessage(typeError);
            return;
        }
        this.handle.handle(this.plugin, sender, arguments, delegator.canvas());
    }
}

