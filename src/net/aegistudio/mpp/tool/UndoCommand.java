package net.aegistudio.mpp.tool;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class UndoCommand extends ActualHandle {
	{ description = "Undo a unexpected action when you regret."; }
	
	public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
	public String noModifiedCanvas = ChatColor.RED + "You should specify a canvas or paint on some canvas before undoing.";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot undo on " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String NOTHING_TO_UNDO = "nothingToUndo";
	public String nothingToUndo = ChatColor.RED + "You have nothing to undo on canvas " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public static final String NO_UNDO_PERMISSION = "noUndoPermission";
	public String noUndoPermission = ChatColor.RED + "You don't have permission to undo on canvas "
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public static final String UNDO_FINISH = "undoFinish";
	public String undoFinish = "You have undone " + ChatColor.UNDERLINE + "$memoto" + ChatColor.RESET + " on canvas "
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
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
