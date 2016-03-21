package net.aegistudio.mpp.control;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public abstract class ConcreteControlCommand extends ActualHandle {
	protected String paramList = "[<parameter>]";
	
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length < 1) 
			sender.sendMessage(prefix + " <name> " + paramList);
		else {
			MapCanvasRegistry canvas;
			if((canvas = painting.canvas.nameCanvasMap.get(arguments[0])) == null) {
				sender.sendMessage(painting.control.canvasNotExists.replace("$canvasName", arguments[0]));
				return true;
			}
			
			String[] subArguments = new String[arguments.length - 1];
			System.arraycopy(arguments, 1, subArguments, 0, arguments.length - 1);
			
			this.subhandle(painting, canvas, sender, subArguments);
		}
		return true;
	}
	
	protected abstract void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments);
}
