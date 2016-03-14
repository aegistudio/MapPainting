package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.md_5.bungee.api.ChatColor;

public class ListCanvasCommand extends ActualHandle {
	{	 description = "Listing all canvases you possess."; 	}
	
	public static final String NO_POSSESS = "noPossess";
	public String noPossess = ChatColor.RED + "You don't possess any canvas.";
	
	public static final String ENTRY = "entry";
	public String entry = ChatColor.AQUA + "$name" + ChatColor.RESET;
	
	public static final String POSSESS = "possess";
	public String possess = "You possess these canvas(es): $list.";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.noPossess = painting.getLocale(NO_POSSESS, noPossess, section);
		this.entry = painting.getLocale(ENTRY, entry, section);
		this.possess = painting.getLocale(POSSESS, possess, section);
	}

	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		StringBuilder list = new StringBuilder();
		boolean first = true;
		for(MapCanvasRegistry canvas : painting.canvas.nameCanvasMap.values())
			if(canvas.owner.equals(sender.getName())) {
				if(first) first = false;
				else list.append(", ");
				list.append(entry.replace("$name", canvas.name));
			}
		if(list.length() > 0)
			sender.sendMessage(possess.replace("$list", new String(list)));
		else sender.sendMessage(noPossess);
		return true;
	}
}
