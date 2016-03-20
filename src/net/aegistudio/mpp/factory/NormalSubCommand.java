package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.BufferedCanvas;
import net.aegistudio.mpp.canvas.Canvas;

public class NormalSubCommand extends ConcreteCreateSubCommand{
	{ description = "square canvas fully covering the map."; paramList = "[<1~128>]"; }
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = ChatColor.RED + 
			"The canvas size you input is not in valid format, please input an integer!";
	
	public static final String OUT_OF_RANGE = "outOfRange";
	public String outOfRange = ChatColor.RED + 
			"The canvas size you input is either too big or too small.";
	
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.create.normal")){
			sender.sendMessage(painting.create.noCreatePermission);
			return null;
		}
		
		int size = 128;
		if(arguments.length > 0) 
			try { size = Integer.parseInt(arguments[0]); }
		catch(Throwable e) {
			sender.sendMessage(invalidFormat);
			return null;
		}
		
		if(size < 1 || size > 128) {
			sender.sendMessage(outOfRange);
			return null;
		}
		
		BufferedCanvas canvas = new BufferedCanvas();
		canvas.size = size;
		canvas.painting = painting;
		canvas.pixel = new byte[canvas.size][canvas.size];
		for(int i = 0; i < canvas.size; i ++)
			for(int j = 0; j < canvas.size; j ++)
				canvas.pixel[i][j] = (byte)0x22;
		return canvas;
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
		this.outOfRange = painting.getLocale(OUT_OF_RANGE, outOfRange, section);
	}
}
