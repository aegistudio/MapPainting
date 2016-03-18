package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.BufferedCanvas;
import net.aegistudio.mpp.canvas.Canvas;

public class NormalSubCommand extends ConcreteCreateSubCommand{
	{ description = "square canvas fully covering the map."; paramList = ""; }
	
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.create.normal")){
			sender.sendMessage(painting.create.noCreatePermission);
			return null;
		}
		
		BufferedCanvas canvas = new BufferedCanvas();
		canvas.size = 128;
		canvas.painting = painting;
		canvas.pixel = new byte[canvas.size][canvas.size];
		for(int i = 0; i < canvas.size; i ++)
			for(int j = 0; j < canvas.size; j ++)
				canvas.pixel[i][j] = (byte)0x22;
		return canvas;
	}
	
}
