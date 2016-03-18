package net.aegistudio.mpp.factory;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class CloneSubCommand extends ConcreteCreateSubCommand{
	{ description = "an independent copy of a canvas."; paramList = "<cloned>"; }
	
	public static final String CLONED_UNSPECIFIED = "clonedUnspecified";
	public String clonedUnspecified = "You should specify a the name of map to clone!";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot clone " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.create.clone")){
			sender.sendMessage(painting.create.noCreatePermission);
			return null;
		}
		
		if(arguments.length == 0) {
			sender.sendMessage(clonedUnspecified);
			return null;
		}
		else {
			String name = arguments[0];
			MapCanvasRegistry entry = painting.canvas.nameCanvasMap.get(name);
			if(entry == null) {
				sender.sendMessage(canvasNotExists.replace("$canvasName", name));
				return null;
			}
			return entry.canvas.clone();
		}
	}

	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		
		this.clonedUnspecified = painting.getLocale(CLONED_UNSPECIFIED, clonedUnspecified, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
	}
}
