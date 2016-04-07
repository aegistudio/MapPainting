package net.aegistudio.mpp.export;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * <p>A customized command handle, and the most common command handle. When the MapPainting plugin
 * capture a call to this command, it will organize your plugin, the call prefix, the sender and
 * the arguments as parameters, so that you could respond to it later.</p>
 * 
 * <p>You could register this command handle by calling <code>PluginCommandService.register</code>
 * or <code>PluginCommandService.registerControl</code>, passing an instance implementing 
 * this interface as a parameter. </p>
 * 
 * @author aegistudio
 * 
 * @see net.aegistudio.mpp.export.PluginCommandService
 *
 * @param <P> the owner plugin, always your plugin.
 */

public interface CommandHandle<P extends Plugin> {
	/**
	 * @return the description of this command.
	 */
	public String description();
	
	/**
	 * Handle a command call.
	 * @param plugin the plugin itself
	 * @param prefix the prefix of the command call
	 * @param sender who sends the command call
	 * @param arguments the input argument
	 * @return return true if success, or false if failed.
	 */
	public boolean handle(P plugin, String prefix, CommandSender sender, String[] arguments);
}
