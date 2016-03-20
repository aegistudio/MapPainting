package net.aegistudio.mpp.control;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class TapControlCommand extends ActualHandle {
	{	description = "tap on a pixel with a color.";	}
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 5) return false;
		
		return false;
	}
}
