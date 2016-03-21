package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.script.ScriptCanvas;

public class ScriptSubCommand extends ConcreteCreateSubCommand {
	{ description = "create a javascript-based canvas."; paramList = "<js>"; }
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			return null;
		}
		else {
			String script = arguments[0];
			try {
				ScriptCanvas canvas = new ScriptCanvas(painting);
				canvas.setScript(script);
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
