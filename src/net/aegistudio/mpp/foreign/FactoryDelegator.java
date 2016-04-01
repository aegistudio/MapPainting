package net.aegistudio.mpp.foreign;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.export.CanvasHandle;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.factory.ConcreteCreateSubCommand;

public class FactoryDelegator<P extends Plugin, C extends PluginCanvas> extends ConcreteCreateSubCommand implements Delegated {
	private final P plugin;
	private final CanvasHandle<P, C> handle;
	private final String factory;
	
	public FactoryDelegator(P plugin, CanvasHandle<P, C> handle, String factory) {
		this.plugin = plugin;
		this.handle = handle;
		this.description =  "[" + ChatColor.RED + plugin.getName() 
			+ ChatColor.RESET + "] " + this.handle.description();
		this.paramList = handle.paramList();
		this.factory = factory;
	}
	
	@Override
	public String getPlugin() {
		return plugin.getName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		try {
			CanvasDelegator<C> delegator = (CanvasDelegator<C>) 
					painting.foreignCanvas.generate(plugin, factory, PluginCanvas.class);
			return handle.handle(plugin, sender, arguments, (C) delegator.canvas())? delegator : null;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
