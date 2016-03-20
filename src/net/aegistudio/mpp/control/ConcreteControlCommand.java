package net.aegistudio.mpp.control;

import org.bukkit.command.CommandSender;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class ConcreteControlCommand extends ActualHandle {

	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		return false;
	}

}
