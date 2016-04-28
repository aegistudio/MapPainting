package net.aegistudio.mpp.canvas;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

/**
 * Purge any *.mpp or *dat file which is already
 * removed from this plugin.
 * 
 * @author aegistudio
 */

public class PurgeCanvasCommand extends ActualHandle {
	{	description = "@purge.description"; 	}
	
	public static final String NO_PURGE_PERMISSION = "noPurgePermission";
	public String noPurgePermission = "@purge.noPurgePermission";

	public static final String PURGE_RESULT = "purgeResult";
	public String purgeResult = "@purge.result";
	
	public static final String PURGE_ON_UNLOAD = "purgeOnUnload";
	public boolean purgeOnUnload = true;
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.manager")) {
			sender.sendMessage(noPurgePermission);
			return true;
		}
		
		int purgedCount = 0;
		for(File file : painting.getDataFolder().listFiles((file) -> 
			file.getName().endsWith(".mpp") || file.getName().endsWith(".dat"))) {
			
			String filename = file.getName();
			filename = filename.substring(filename.lastIndexOf('/') + 1);
			filename = filename.substring(0, filename.lastIndexOf('.'));
			
			if(!painting.canvas.nameCanvasMap.containsKey(filename)) {
				file.delete();
				purgedCount ++;
			}
		}
		sender.sendMessage(purgeResult.replace("$count", Integer.toString(purgedCount)));
		
		return true;
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.noPurgePermission = painting.getLocale(NO_PURGE_PERMISSION, noPurgePermission, section);
		this.purgeResult = painting.getLocale(PURGE_RESULT, purgeResult, section);
		
		if(section.contains(PURGE_ON_UNLOAD))	this.purgeOnUnload = section.getBoolean(PURGE_ON_UNLOAD);
		else section.set(PURGE_ON_UNLOAD, purgeOnUnload);
	}
	
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {
		super.save(painting, section);
		if(purgeOnUnload)
			this.handle(painting, "", painting.getServer().getConsoleSender(), new String[0]);
	}
}
