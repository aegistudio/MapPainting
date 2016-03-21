package net.aegistudio.mpp.script;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.control.ConcreteControlCommand;

public class RebootControlCommand extends ConcreteControlCommand {
	{ description = "reboot a script canvas."; }
	
	@Override
	protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
		
	}
	
}
