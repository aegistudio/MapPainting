package net.aegistudio.mpp.export;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface CommandHandle<P extends Plugin> {
	public String description();
	
	public boolean handle(P plugin, String prefix, CommandSender sender, String[] arguments);
}
