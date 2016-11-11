package net.aegistudio.mpp.foreign;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import net.aegistudio.mpp.CompositeHandle;

public class CompositeDelegator extends CompositeHandle implements Delegated {
	public Plugin plugin;
	public CompositeDelegator(Plugin plugin, String description) {
		this.plugin = plugin;
		super.description = "[" + ChatColor.RED + plugin.getName() 
			+ ChatColor.RESET + "] " + description;
	}
	@Override
	public String getPlugin() {
		return plugin.getName();
	}
}
