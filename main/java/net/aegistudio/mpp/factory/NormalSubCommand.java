package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.Color;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.BufferedCanvas;
import net.aegistudio.mpp.canvas.Canvas;

public class NormalSubCommand extends ConcreteCreateSubCommand{
	{ description = "@create.normal.description"; paramList = "[<1~128>] [<background>]"; }
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = "@create.normal.invalidFormat";
	
	public static final String OUT_OF_RANGE = "outOfRange";
	public String outOfRange = "@create.normal.outOfRange";
	
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
		
		Color color = Color.WHITE;
		if(arguments.length > 1) 
			try { color = painting.color.parseColor(arguments[1]).color; }
			catch(Throwable e) {
				sender.sendMessage(invalidFormat);
				return null;
			}
		
		byte canvasColor = (byte) painting.canvas.color.getIndex(color);
				
		BufferedCanvas canvas = new BufferedCanvas(painting);
		canvas.size = size;
		canvas.pixel = new byte[canvas.size][canvas.size];
		for(int i = 0; i < canvas.size; i ++)
			for(int j = 0; j < canvas.size; j ++)
				canvas.pixel[i][j] = canvasColor;
		return canvas;
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
		this.outOfRange = painting.getLocale(OUT_OF_RANGE, outOfRange, section);
	}
}
