package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;

public interface CanvasFactory {
	public Canvas create();
	
	public Canvas create(CommandSender issue, String[] arguments);
}
