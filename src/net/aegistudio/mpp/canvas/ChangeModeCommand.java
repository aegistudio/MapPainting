package net.aegistudio.mpp.canvas;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class ChangeModeCommand extends ActualHandle {
	{	description = "@chmod.description"; 	}

	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = "@chmod.onlyPlayer";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@chmod.canvasNotExists";
	
	public static final String PAINTER_MODIFIED = "painterModified";
	public String painterModified = "@chmod.painterModified";
	
	public static final String PAINTER_ADDED = "painterAdded";
	public String painterAdded = "@chmod.painterAdded";
	public static final String PAINTER_REMOVED = "painterRemoved";
	public String painterRemoved = "@chmod.painterRemoved";
	
	public static final String INTERACTOR_ADDED = "interactorAdded";
	public String interactorAdded = "@chmod.interactorAdded";
	public static final String INTERACTOR_REMOVED = "interactorRemoved";
	public String interactorRemoved = "@chmod.interactorRemoved";
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = "@chmod.notHolding";
	
	public static final String NO_CHMOD_PERMISSION = "noChmodPermission";
	public String noChmodPermission = "@chmod.noChmodPermission";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.painterModified = painting.getLocale(PAINTER_MODIFIED, painterModified, section);
		
		this.painterAdded = painting.getLocale(PAINTER_ADDED, painterAdded, section);
		this.painterRemoved = painting.getLocale(PAINTER_REMOVED, painterRemoved, section);
		this.interactorAdded = painting.getLocale(INTERACTOR_ADDED, interactorAdded, section);
		this.interactorRemoved = painting.getLocale(INTERACTOR_REMOVED, interactorRemoved, section);
		
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.noChmodPermission = painting.getLocale(NO_CHMOD_PERMISSION, noChmodPermission, section);
	}
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage(prefix + " [p|i]<+/-who>... [<name>]");
			return true;
		}
		
		ArrayList<String> addPainter = new ArrayList<String>();
		ArrayList<String> removePainter = new ArrayList<String>();
		ArrayList<String> addInteractor = new ArrayList<String>();
		ArrayList<String> removeInteractor = new ArrayList<String>();
		
		String name = null;
		for(String argument : arguments) {
			char first = argument.charAt(0);
			if(first == '+') addPainter.add(argument.substring(1));
			else if(first == '-') removePainter.add(argument.substring(1));
			else if(argument.startsWith("p+")) addPainter.add(argument.substring(2));
			else if(argument.startsWith("p-")) removePainter.add(argument.substring(2));
			else if(argument.startsWith("i+")) addInteractor.add(argument.substring(2));
			else if(argument.startsWith("i-")) removeInteractor.add(argument.substring(2));
			else { name = argument; break; }
		}
		
		MapCanvasRegistry registry;
		if(name == null) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayer);
				return true;
			}
			
			Player player = (Player) sender;
			registry = painting.canvas.holding(player);
			if(registry == null) {
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
			if(registry.painter.add(painter)) 
				tell(painting, painter, painterAdded, registry);
		
		for(String painter : removePainter)
			if(registry.painter.remove(painter))
				tell(painting, painter, painterRemoved, registry);
		
		for(String interactor : addInteractor)
			if(registry.interactor.add(interactor)) 
				tell(painting, interactor, interactorAdded, registry);
		
		for(String interactor : removeInteractor)
			if(registry.interactor.remove(interactor)) 
				tell(painting, interactor, interactorRemoved, registry);
		
		sender.sendMessage(painterModified.replace("$canvasName", registry.name));
		painting.ackHistory(registry, sender);
		return true;
	}
	
	public void tell(MapPainting painting, String who, String totell, MapCanvasRegistry registry) {
		Player target = painting.getServer().getPlayer(who);
		if(target != null) target.sendMessage(totell.replace("$canvasName", registry.name));
	}
}
