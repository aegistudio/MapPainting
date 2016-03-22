package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.script.ScriptCanvas;

public class ScriptSubCommand extends ConcreteCreateSubCommand {
	{ description = "a incredible canvas run on a script."; paramList = "<script> [<language>]"; }
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			return null;
		}
		else {
			try {
				ScriptCanvas canvas = new ScriptCanvas(painting);
				canvas.filename = arguments[0];
				canvas.setLanguage(arguments.length > 1? arguments[1] : "ECMAScript");
				canvas.setScript();
				canvas.reboot();
				return canvas;
			}
			catch(Exception e) {
				sender.sendMessage(e.getMessage());
				return null;
			}
		}
	}
}
