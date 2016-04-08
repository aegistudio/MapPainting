package net.aegistudio.mpp.canvas;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class ChangeModeCommand extends ActualHandle {
	{	description = "Manage the painters of a canvas."; 	}

	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use change mode command without specifying canvas name!";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot add or remove painters of " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RED + "! Specified canvas " + ChatColor.AQUA + "$canvasName"
			+ ChatColor.RED + " doesn't exist!";
	
	public static final String PAINTER_MODIFIED = "painterModified";
	public String painterModified = "You have successfully manages painters of " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RESET + "!";
	
	public static final String PAINTER_ADDED = "painterAdded";
	public String painterAdded = "You are added as the painter of " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	public static final String PAINTER_REMOVED = "painterRemoved";
	public String painterRemoved = "You are no longer the painter of " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = ChatColor.RED + "You should either hold a canvas in hand or specify the canvas name!";
	
	public static final String NO_CHMOD_PERMISSION = "noChmodPermission";
	public String noChmodPermission = ChatColor.RED + "You don't have permission to manage painters of " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.painterModified = painting.getLocale(PAINTER_MODIFIED, painterModified, section);
		this.painterAdded = painting.getLocale(PAINTER_ADDED, painterAdded, section);
		this.painterRemoved = painting.getLocale(PAINTER_REMOVED, painterRemoved, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.noChmodPermission = painting.getLocale(NO_CHMOD_PERMISSION, noChmodPermission, section);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage(prefix + " <+/-painter>... [<name>]");
			return true;
		}
		
		ArrayList<String> addPainter = new ArrayList<String>();
		ArrayList<String> removePainter = new ArrayList<String>();
		String name = null;
		for(String argument : arguments) {
			char first = argument.charAt(0);
			if(first == '+') addPainter.add(argument.substring(1));
			else if(first == '-') removePainter.add(argument.substring(1));
			else { name = argument; break; }
		}
		
		MapCanvasRegistry registry;
		if(name == null) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayer);
				return true;
			}
			Player player = (Player) sender;
			ItemStack item = player.getItemInHand();
			
			if(item.getType() != Material.MAP ||
					null == (registry = painting.canvas.idCanvasMap.get(item.getDurability()))) {
				sender.sendMessage(notHolding);
				return true;
			}
		}
		else {
			registry = painting.canvas.nameCanvasMap.get(name);
			if(registry == null) {
				sender.sendMessage(canvasNotExists.replace("$canvasName", name));
				return true;
			}
		}
		
		if(!registry.hasPermission(sender, "chmod")) {
			sender.sendMessage(noChmodPermission.replace("$canvasName", registry.name));
			return true;
		}
		
		for(String painter : addPainter)
			if(registry.painter.add(painter)) {
				Player addedPainter = painting.getServer().getPlayer(painter);
				if(addedPainter != null) addedPainter.sendMessage(painterAdded
						.replace("$canvasName", registry.name));
			}
		
		for(String painter : removePainter)
			if(registry.painter.remove(painter)) {
				Player removedPainter = painting.getServer().getPlayer(painter);
				if(removedPainter != null) removedPainter.sendMessage(painterRemoved
						.replace("$canvasName", registry.name));
			}
		
		sender.sendMessage(painterModified.replace("$canvasName", registry.name));
		painting.ackHistory(registry, sender);
		return true;
	}
}
