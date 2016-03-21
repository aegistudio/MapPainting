package net.aegistudio.mpp.control;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.CanvasWrapper;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class WrapControlCommand extends ConcreteControlCommand {
	{	description = "change the wrapped canvas of a wrapper.";	paramList = "<newWrapped> [<layer>]";  }
	public static final String NOT_WRAPPER = "notWrapper";
	public String notWrapper = ChatColor.RED + "The specified cavas " + ChatColor.AQUA + "$canvasName" 
			+ ChatColor.RED + " is not a wrapper!";
	
	@Override
	protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
		
		if(!sender.hasPermission("mpp.control.wrap")) {
			sender.sendMessage(painting.control.noControlPermission.replace("$canvasName", canvas.name));
			return;
		}
		
		int layer = 0;
		if(arguments.length < 1 || arguments.length > 2) {
			sender.sendMessage(painting.control.invalidArguments);
			return;
		}
		
		if(arguments.length == 2) try {
			layer = Integer.parseInt(arguments[1]);
		}
		catch(Throwable throwable) {
			sender.sendMessage(painting.control.invalidFormat);
			return;
		}
		
		if(!(canvas.canvas instanceof CanvasWrapper)) {
			sender.sendMessage(notWrapper.replace("$canvas", canvas.name));
			return;
		}
		else ((CanvasWrapper)(canvas.canvas)).setWrapping(layer, arguments[0]);
	}
}