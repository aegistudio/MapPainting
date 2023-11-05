/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.tool;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class UndoCommand
extends ActualHandle {
    public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
    public String noModifiedCanvas;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String NOTHING_TO_UNDO = "nothingToUndo";
    public String nothingToUndo;
    public static final String NO_UNDO_PERMISSION = "noUndoPermission";
    public String noUndoPermission;
    public static final String UNDO_FINISH = "undoFinish";
    public String undoFinish;

    public UndoCommand() {
        this.description = "@undo.description";
        this.noModifiedCanvas = "@undo.noModifiedCanvas";
        this.canvasNotExists = "@undo.canvasNotExists";
        this.nothingToUndo = "@undo.nothingToUndo";
        this.noUndoPermission = "@undo.noUndoPermission";
        this.undoFinish = "@undo.undoFinish";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.nothingToUndo = painting.getLocale(NOTHING_TO_UNDO, this.nothingToUndo, section);
        this.noUndoPermission = painting.getLocale(NO_UNDO_PERMISSION, this.noUndoPermission, section);
        this.undoFinish = painting.getLocale(UNDO_FINISH, this.undoFinish, section);
        this.noModifiedCanvas = painting.getLocale(NO_MODIFIED_CANVAS, this.noModifiedCanvas, section);
        this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        String result;
        String canvas = null;
        if (arguments.length > 0) {
            canvas = arguments[0];
            painting.m_canvasManager.latest.put(sender.getName(), canvas);
        } else {
            canvas = painting.m_canvasManager.latest.get(sender.getName());
        }
        if (canvas == null) {
            sender.sendMessage(this.noModifiedCanvas);
            return true;
        }
        MapCanvasRegistry target = painting.m_canvasManager.nameCanvasMap.get(canvas);
        if (target == null) {
            sender.sendMessage(this.canvasNotExists.replace("$canvasName", canvas));
            return true;
        }
        if (!sender.hasPermission("mpp.manager")) {
            if (!sender.hasPermission("mpp.undo")) {
                sender.sendMessage(this.noUndoPermission.replace("$canvasName", canvas));
                return true;
            }
            if (!target.owner.equals(sender.getName()) && !target.canPaint(sender)) {
                sender.sendMessage(this.noUndoPermission.replace("$canvasName", canvas));
                return true;
            }
        }
        if ((result = target.history.undo()) == null) {
            sender.sendMessage(this.nothingToUndo.replace("$canvasName", canvas));
        } else {
            sender.sendMessage(this.undoFinish.replace("$canvasName", canvas).replace("$memoto", result));
        }
        return true;
    }
}

