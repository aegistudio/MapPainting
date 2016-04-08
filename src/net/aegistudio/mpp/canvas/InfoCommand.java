package net.aegistudio.mpp.canvas;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class InfoCommand extends ActualHandle {
	{		description = "get detailed information of a canvas.";	}
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = ChatColor.RED + "You should either hold a canvas in hand or specify the canvas name!";
	
	public static final String NAME = "name";
	public String name = ChatColor.BOLD + "Name: " + ChatColor.RESET + ChatColor.AQUA + "$name" + ChatColor.RESET + "($binding)";
	
	public static final String OWNER = "owner";
	public String owner = ChatColor.BOLD + "Owner: " + ChatColor.RESET + ChatColor.GREEN + "$owner";
	
	public static final String PAINTER = "painter";
	public String painter = ChatColor.BOLD + "Painter: " + ChatColor.RESET + "$painterList";
	
	public static final String PAINTER_LISTITEM = "painterListitem";
	public String painterListitem = ChatColor.GREEN + "$painter" + ChatColor.RESET;
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		MapCanvasRegistry registry = null;
		if(arguments.length == 0) {
			if(sender instanceof Player)
				registry = painting.canvas.holding((Player) sender);
		}
		else registry = painting.canvas.nameCanvasMap.get(arguments[0]);
		
		if(registry == null) {
			sender.sendMessage(notHolding);
			return true;
		}
		
		sender.sendMessage(name.replace("$name", registry.name)
				.replace("$binding", Short.toString(registry.binding)));
		
		sender.sendMessage(owner.replace("$owner", registry.owner));
		
		StringBuilder painterList = new StringBuilder();
		boolean first = true;
		for(String painter : registry.painter) {
			if(first) first = false;
			else painterList.append(", ");
			painterList.append(painterListitem.replace("$painter", painter));
		}
		sender.sendMessage(painter.replace("$painterList", new String(painterList)));
		
		return true;
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.name = painting.getLocale(NAME, name, section);
		this.owner = painting.getLocale(OWNER, owner, section);
		this.painter = painting.getLocale(PAINTER, painter, section);
		this.painterListitem = painting.getLocale(PAINTER_LISTITEM, painterListitem, section);
	}
}