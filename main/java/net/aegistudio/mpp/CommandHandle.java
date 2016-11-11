package net.aegistudio.mpp;

import org.bukkit.command.CommandSender;

public interface CommandHandle extends Module {
	public String description();
	
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments);
}
