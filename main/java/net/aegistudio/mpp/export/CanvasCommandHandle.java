package net.aegistudio.mpp.export;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * <p>A customized command handle, which require a canvas of specific type as target
 *  to be processed. This command handle is used when registering customized create or 
 * control commands. </p>
 * 
 * <p>You could register this command handle by calling <code>PluginCommandService.registerCreate</code>,
 * passing an instance implementing this interface as a parameter.</p>
 * 
 * @author aegistudio
 *
 * @see net.aegistudio.mpp.export.PluginCommandService
 * 
 * @param <P> the owner plugin, always your plugin.
 * @param <C> the canvas which could be passed into the handle.
 */

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
	 * Handle a canvas command call.
	 * @param plugin the plugin itself
	 * @param sender who sends the command call.
	 * @param arguments the input argument.
	 * @param canvas the input canvas.
	 * @return return true if success, or false if failed.
	 */
	public boolean handle(P plugin, CommandSender sender, String[] arguments, C canvas);
}
