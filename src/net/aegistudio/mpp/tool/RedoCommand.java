package net.aegistudio.mpp.tool;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class RedoCommand extends ActualHandle {
	{ description = "@redo.description"; }
	
	public static final String NO_MODIFIED_CANVAS = "noModifiedCanvas";
	public String noModifiedCanvas = "@redo.noModifiedCanvas";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@redo.canvasNotExists";
	
	public static final String NOTHING_TO_REDO = "nothingToRedo";
	public String nothingToRedo = "@redo.nothingToRedo";
			
	public static final String NO_REDO_PERMISSION = "noRedoPermission";
	public String noRedoPermission = "@redo.noRedoPermission";
	
	public static final String REDO_FINISH = "redoFinish";
	public String redoFinish = "@redo.redoFinish";
	
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
