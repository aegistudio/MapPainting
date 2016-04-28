package net.aegistudio.mpp.control;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.color.PseudoColor;
import net.aegistudio.mpp.tool.PixelTapMemento;

public class TapControlCommand extends ConcreteControlCommand {
	{	description = "@control.tap.description";	paramList = "<x> <y> <color>";  }
	
	@Override
	protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.control.tap")) {
			sender.sendMessage(painting.control.noControlPermission.replace("$canvasName", canvas.name));
			return;
		}
		
		if(arguments.length != 3) {
			sender.sendMessage(painting.control.invalidArguments);
			return;
		}
		
		int x, y;
		PseudoColor color;
		try {
			x = Integer.parseInt(arguments[0]);
			y = Integer.parseInt(arguments[1]);
			color = painting.color.parseColor(arguments[2]);
		}
		catch(Throwable t) {
			sender.sendMessage(painting.control.invalidFormat);
			return;
		}
		
		Interaction interaction = new Interaction(x, y, sender, null, null, false);
		canvas.history.add(new PixelTapMemento(canvas.canvas, interaction, color.color, paramList));
		painting.ackHistory(canvas, sender);
	}
}