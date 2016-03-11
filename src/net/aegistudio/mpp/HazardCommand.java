package net.aegistudio.mpp;

import org.bukkit.command.CommandSender;

public interface HazardCommand extends CommandHandle {
	public void handle(MapPainting painting, CommandSender sender, Object hazardState);
}
