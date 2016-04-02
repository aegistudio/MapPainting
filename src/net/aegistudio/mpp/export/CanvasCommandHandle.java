package net.aegistudio.mpp.export;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public interface CanvasCommandHandle<P extends Plugin, C extends PluginCanvas> {
	/**
	 * @return the description of this command.
	 */
	public String description();
	
	/**
	 * @return param list of this command.
	 */
	public String paramList();
	
	/**
	 * @param plugin the plugin itself
	 * @param sender who sends the command.
	 * @param arguments the input argument
	 * @param canvas the input canvas.
	 * @return return true when success, or false when failed.
	 */
	public boolean handle(P plugin, CommandSender sender, String[] arguments, C canvas);
}
