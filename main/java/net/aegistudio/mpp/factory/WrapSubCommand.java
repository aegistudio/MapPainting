/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp.factory;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.WrapCanvas;
import org.bukkit.command.CommandSender;

public class WrapSubCommand
extends ConcreteCreateSubCommand {
    public WrapSubCommand() {
        this.description = "@create.wrap.description";
        this.paramList = "";
    }

    @Override
    protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
        WrapCanvas canvas = new WrapCanvas(painting);
        return canvas;
    }
}

