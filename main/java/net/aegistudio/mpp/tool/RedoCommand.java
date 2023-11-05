
package net.aegistudio.mpp.tool;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class RedoCommand
extends ActualHandle {
    public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
    public String noModifiedCanvas;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String NOTHING_TO_REDO = "nothingToRedo";
    public String nothingToRedo;
    public static final String NO_REDO_PERMISSION = "noRedoPermission";
    public String noRedoPermission;
    public static final String REDO_FINISH = "redoFinish";
    public String redoFinish;

    public RedoCommand() {
        this.description = "@redo.description";
        this.noModifiedCanvas = "@redo.noModifiedCanvas";
        this.canvasNotExists = "@redo.canvasNotExists";
        this.nothingToRedo = "@redo.nothingToRedo";
        this.noRedoPermission = "@redo.noRedoPermission";
        this.redoFinish = "@redo.redoFinish";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.nothingToRedo = painting.getLocale(NOTHING_TO_REDO, this.nothingToRedo, section);
        this.noRedoPermission = painting.getLocale(NO_REDO_PERMISSION, this.noRedoPermission, section);
        this.redoFinish = painting.getLocale(REDO_FINISH, this.redoFinish, section);
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
            if (!sender.hasPermission("mpp.redo")) {
                sender.sendMessage(this.noRedoPermission.replace("$canvasName", canvas));
                return true;
            }
            if (!target.owner.equals(sender.getName()) && !target.canPaint(sender)) {
                sender.sendMessage(this.noRedoPermission.replace("$canvasName", canvas));
                return true;
            }
        }
        if ((result = target.history.redo()) == null) {
            sender.sendMessage(this.nothingToRedo.replace("$canvasName", canvas));
        } else {
            sender.sendMessage(this.redoFinish.replace("$canvasName", canvas).replace("$memoto", result));
        }
        return true;
    }
}

