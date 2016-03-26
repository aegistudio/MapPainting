package net.aegistudio.mpp.control;

import java.awt.Color;
import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.tool.PixelTapMemento;

public class TapControlCommand extends ConcreteControlCommand {
	{	description = "tap on a pixel with a color.";	paramList = "<x> <y> <r> <g> <b>";  }
	
	@Override
	protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.control.tap")) {
			sender.sendMessage(painting.control.noControlPermission.replace("$canvasName", canvas.name));
			return;
		}
		
		if(arguments.length != 5) {
			sender.sendMessage(painting.control.invalidArguments);
			return;
		}
		
		int x, y, r, g, b;
		try {
			x = Integer.parseInt(arguments[0]);
			y = Integer.parseInt(arguments[1]);
			r = Integer.parseInt(arguments[2]);
			g = Integer.parseInt(arguments[3]);
			b = Integer.parseInt(arguments[4]);
		}
		catch(Throwable t) {
			sender.sendMessage(painting.control.invalidFormat);
			return;
		}
		
		Interaction interaction = new Interaction(x, y, sender, null, null, false);
		canvas.history.add(new PixelTapMemento(canvas.canvas, interaction, new Color(r, g, b), paramList));
	}
}