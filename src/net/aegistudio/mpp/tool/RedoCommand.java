package net.aegistudio.mpp.tool;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class RedoCommand extends ActualHandle {
	{ description = "Do a recently undone action again."; }
	
	public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
	public String noModifiedCanvas = ChatColor.RED + "You should specify a canvas or paint on some canvas before redoing.";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot redo on " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String NOTHING_TO_REDO = "nothingToRedo";
	public String nothingToRedo = ChatColor.RED + "You have nothing to redo on canvas " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public static final String NO_REDO_PERMISSION = "noRedoPermission";
	public String noRedoPermission = ChatColor.RED + "You don't have permission to redo on canvas "
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public static final String REDO_FINISH = "redoFinish";
	public String redoFinish = "You have redone " + ChatColor.UNDERLINE + "$memoto" + ChatColor.RESET + " on canvas "
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.nothingToRedo = painting.getLocale(NOTHING_TO_REDO, nothingToRedo, section);
		this.noRedoPermission = painting.getLocale(NO_REDO_PERMISSION, noRedoPermission, section);
		this.redoFinish = painting.getLocale(REDO_FINISH, redoFinish, section);
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
			if(!sender.hasPermission("mpp.redo")) {
				sender.sendMessage(this.noRedoPermission.replace("$canvasName", canvas));
				return true;
			}
			
			if(!target.owner.equals(sender.getName()) && !target.canPaint(sender)) {
				sender.sendMessage(this.noRedoPermission.replace("$canvasName", canvas));
				return true;
			}
		}
		
		String result = target.history.redo();
		if(result == null) 
			sender.sendMessage(nothingToRedo.replace("$canvasName", canvas));
		else sender.sendMessage(redoFinish.replace("$canvasName", canvas).replace("$memoto", result));
		
		return true;
	}
}
