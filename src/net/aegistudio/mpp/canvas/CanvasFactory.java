package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.MapPainting;

public interface CanvasFactory {
	public Canvas create();
	
	public Canvas create(MapPainting painting, CommandSender issue, String[] arguments);
}
