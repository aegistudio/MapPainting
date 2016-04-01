package net.aegistudio.mpp.export;

import org.bukkit.plugin.Plugin;

/**
 * Provide service for register commands to map painting.
 * Registered command will be identified by a plugin name before the description.
 * 
 * The attachment point is in the form of "&lt;commandParent&gt;/subcommand.", like
 * "create/mycanvas", will register a mycanvas command under the create command.
 * 
 * @author aegistudio
 */

public interface PluginCommandService {
	/**
	 * Register a command service to specified command attach point.
	 * @param thiz the plugin it self.
	 * @param attach the attach point.
	 * @param command the command to attach.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin> void register(P thiz, String attach, CommandHandle<P> command) throws NamingException;
	
	/**
	 * Unregister a command service from specified attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @return removal success.
	 */
	public <P extends Plugin> boolean unregister(P thiz, String attach);
	
	/**
	 * Register a group to specified command attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param the description.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin> void registerGroup(P thiz, String attach, String description) throws NamingException;
	
	/**
	 * Register a ccreate canvas command to specified command attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param identifier the factory identifier.
	 * @param create the create command handle.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin, C extends PluginCanvas> void registerCreate(P thiz, String attach, 
			String identifier, CanvasHandle<P, C> create) throws NamingException;
}
