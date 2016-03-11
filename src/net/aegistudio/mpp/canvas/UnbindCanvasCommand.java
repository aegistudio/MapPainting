package net.aegistudio.mpp.canvas;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class UnbindCanvasCommand extends ActualHandle {
	{ description = "Unbind a map from a canvas, making it a normal map again."; }
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot unbind " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String NO_PERMISSION = "noPermission";
	public String noPermission = ChatColor.RED + "You are neither the owner of the painting " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + " nor the manager!";
	
	public static final String UNBOUND = "unbound";
	public String unbound = "The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + " has been unbound.";
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 1)
			sender.sendMessage(prefix + " <name>");
		else {

			MapCanvasRegistry canvas = painting.canvas.nameCanvasMap.get(arguments[0]);
			if(canvas == null) { 
				sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[0]));
				return true;
			}

			if(!sender.hasPermission("mpp.manager")) 
				if(!canvas.owner.equals(sender.getName())) {
					sender.sendMessage(noPermission.replace("$canvasName", arguments[0]));
					return true;
				}
			
			canvas.remove();
			
			painting.canvas.idCanvasMap.remove(canvas.binding);
			painting.canvas.nameCanvasMap.remove(canvas.name);
			
			sender.sendMessage(unbound.replace("$canvasName", arguments[0]));
		}
		return true;
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		canvasNotExists = super.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		noPermission = super.getLocale(NO_PERMISSION, noPermission, section);
		unbound = super.getLocale(UNBOUND, unbound, section);
	}
}
