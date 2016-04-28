package net.aegistudio.mpp.canvas;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class GiveCanvasCommand extends ActualHandle {
	{ description = "Give you the canvas you want."; }
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use give command.";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot give " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"to you! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String NO_PERMISSION = "noPermission";
	public String noPermission = ChatColor.RED + "You are neither the owner of the painting " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + " nor the manager!";
	
	public static final String HERE_YOU_ARE = "hereYouAre";
	public String hereYouAre = "Here you are! The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage(prefix + " <name>");
			return true;
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(onlyPlayer);
			return true;
		}
		
		MapCanvasRegistry canvas = painting.getCanvas(arguments[0], sender);
		if(canvas == null) { 
			sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[0]));
			return true;
		}

		if(!canvas.hasPermission(sender, "give")) {
			sender.sendMessage(noPermission.replace("$canvasName", arguments[0]));
			return true;
		}
		
		painting.canvas.give((Player) sender, canvas);
		sender.sendMessage(hereYouAre.replace("$canvasName", arguments[0]));
		return true;
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		noPermission = painting.getLocale(NO_PERMISSION, noPermission, section);
		onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		hereYouAre = painting.getLocale(HERE_YOU_ARE, hereYouAre, section);
	}
}
