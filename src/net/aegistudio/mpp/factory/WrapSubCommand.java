package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.WrapCanvas;

public class WrapSubCommand extends ConcreteCreateSubCommand {
	{	description = "a wrapping container for canvases.";	 paramList = ""; }
	
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		WrapCanvas canvas = new WrapCanvas();
		canvas.painting = painting;
		if(arguments.length > 0)
			canvas.wrapping = arguments[0];
		return canvas;
	}
}
