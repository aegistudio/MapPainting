package net.aegistudio.mpp.export;

import org.bukkit.plugin.Plugin;

/**
 * <p>Provides services for registering customized commands to map painting.</p>
 * 
 * <p>The registered is identified by its <b>attach point</b>, a attach point is 
 * in the form of <b><code>[&lt;group&gt;/]&lt;name&gt;</code></b>. E.g.<b><code>create/mycanvas</code></b>
 * referes to a <code>mycanvas</code> command under the create group. And you could
 * issue this command by calling <b>/mpp create mycanvas</b>.</p>
 * 
 * <p>However, when your attach point contains a group, the group need to exist or 
 * be created first (Or a naming exception will be generated). By calling 
 * <code>registerGroup</code> will create such group.</p>
 * 
 * <p>Create commands and control commands are special, and you could register
 * a create or control command by <code>registerCreate</code> or <code>registerControl</code>.</p>
 * 
 * <p>The following code depicts create a command group <i>myplugin</i>, register a common command <i>mycmd</i>, 
 * a create command <i>mynew</i> and a control command <i>myctl</i> to the group, while mirroring <i>myctl</i>
 * under the control group of map painting:</p>
 * 
 * <p><b><code style="font-family: Courier New;">
 * &nbsp;&nbsp;//Retrieve plugin command service provider first.<br>
 * &nbsp;&nbsp;PluginCommandService service = plugin.getServer().getServicesManager()<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.getRegistration(PluginCommandService.class).getProvider();<br>
 * <br>
 * &nbsp;&nbsp;//The myplugin group.<br> 
 * &nbsp;&nbsp;service.registerGroup(plugin, "myplugin", "the myplugin group.");<br>
 * <br>
 * &nbsp;&nbsp;//Common command mycmd.<br> 
 * &nbsp;&nbsp;CommandHandle&lt;MyPlugin&gt; mycmd = new MyCmd();<br>
 * &nbsp;&nbsp;service.register(plugin, "myplugin/mycmd", mycmd);<br>
 * <br>
 * &nbsp;&nbsp;//Create command mynew.<br> 
 * &nbsp;&nbsp;CanvasCommandHandle&lt;MyPlugin, MyCanvas&gt; mynew = new MyCreate();<br>
 * &nbsp;&nbsp;service.register(plugin, "myplugin/mynew", "mycanvas", mynew);<br>
 * <br>
 * &nbsp;&nbsp;//Control command myctl (register to both myplugin and control).<br> 
 * &nbsp;&nbsp;CanvasCommandHandle&lt;MyPlugin, MyCanvas&gt; myctl = new MyControl();<br>
 * &nbsp;&nbsp;service.register(plugin, "myplugin/myctl", "mycanvas", MyCanvas.class, myctl);<br>
 * &nbsp;&nbsp;service.register(plugin, "control/myctl", "mycanvas", MyCanvas.class, myctl);<br>
 * </code></b></p>
 * 
 * @author aegistudio
 */

public interface PluginCommandService {
	/**
	 * Register a command to an attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param command the command to attach.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin> void register(P thiz, String attach, CommandHandle<P> command) throws NamingException;
	
	/**
	 * Unregister a command from an attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @return removal success.
	 */
	public <P extends Plugin> boolean unregister(P thiz, String attach);
	
	/**
	 * Register a group to an attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param description the description.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin> void registerGroup(P thiz, String attach, String description) throws NamingException;
	
	/**
	 * Register a create command to an attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param identifier the factory identifier.
	 * @param create the create command handle.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin, C extends PluginCanvas> void registerCreate(P thiz, String attach, 
			String identifier, CanvasCommandHandle<P, C> create) throws NamingException;
	
	/**
	 * Register a control command an attach point.
	 * @param thiz the plugin itself.
	 * @param attach the attach point.
	 * @param type how do we call the canvases that the control command handle? (not necessarily the factory identifier)
	 * @param canvasClazz the most abstract canvas class should this control command handle.
	 * @param control the control command handle.
	 * @throws NamingException path error, element already exist, etc.
	 */
	public <P extends Plugin, C extends PluginCanvas> void registerControl(P thiz, String attach, String type,
			Class<? extends C> canvasClazz, CanvasCommandHandle<P, C> control) throws NamingException;
}
