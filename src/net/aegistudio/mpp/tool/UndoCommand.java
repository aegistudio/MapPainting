package net.aegistudio.mpp.tool;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class UndoCommand extends ActualHandle {
	{ description = "@undo.description"; }
	
	public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
	public String noModifiedCanvas = "@undo.noModifiedCanvas";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@undo.canvasNotExists";
	
	public static final String NOTHING_TO_UNDO = "nothingToUndo";
	public String nothingToUndo = "@undo.nothingToUndo";
	
	public static final String NO_UNDO_PERMISSION = "noUndoPermission";
	public String noUndoPermission = "@undo.noUndoPermission";
	
	public static final String UNDO_FINISH = "undoFinish";
	public String undoFinish = "@undo.undoFinish";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.nothingToUndo = painting.getLocale(NOTHING_TO_UNDO, nothingToUndo, section);
		this.noUndoPermission = painting.getLocale(NO_UNDO_PERMISSION, noUndoPermission, section);
		this.undoFinish = painting.getLocale(UNDO_FINISH, undoFinish, section);
		this.noModifiedCanvas = painting.getLocale(NO_MODIFIED_CANVAS, noModifiedCanvas, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
	}
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		String canvas = null;
		if(arguments.length > 0) {
			canvas = arguments[0];
			painting.canvas.latest.put(sender.getName(), canvas);
		}
		else canvas = painting.canvas.latest.get(sender.getName());
		if(canvas == null) {
			sender.sendMessage(noModifiedCanvas);
			return true;
		}
		
		MapCanvasRegistry target = painting.canvas.nameCanvasMap.get(canvas);
		if(target == null) {
			sender.sendMessage(canvasNotExists.replace("$canvasName", canvas));
			return true;
		}
		
		if(!sender.hasPermission("mpp.manager")) {
			if(!sender.hasPermission("mpp.undo")) {
				sender.sendMessage(this.noUndoPermission.replace("$canvasName", canvas));
				return true;
			}
			
			if(!target.owner.equals(sender.getName()) && !target.canPaint(sender)) {
				sender.sendMessage(this.noUndoPermission.replace("$canvasName", canvas));
				return true;
			}
		}
		
		String result = target.history.undo();
		if(result == null) 
			sender.sendMessage(nothingToUndo.replace("$canvasName", canvas));
		else sender.sendMessage(undoFinish.replace("$canvasName", canvas).replace("$memoto", result));
		
		return true;
	}
}
