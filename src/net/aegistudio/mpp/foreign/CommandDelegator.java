package net.aegistudio.mpp.foreign;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.export.CommandHandle;

public class CommandDelegator<P extends Plugin> extends ActualHandle implements Delegated {
	private final P plugin;
	private final CommandHandle<P> handle;
	public CommandDelegator(P plugin, CommandHandle<P> handle) {
		this.plugin = plugin;
		this.handle = handle;
		super.description = "[" + ChatColor.RED + plugin.getName() 
			+ ChatColor.RESET + "] " + this.handle.description();
	}
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		return handle.handle(plugin, prefix, sender, arguments);
	}

	@Override
	public String getPlugin() {
		return plugin.getName();
	}
}
