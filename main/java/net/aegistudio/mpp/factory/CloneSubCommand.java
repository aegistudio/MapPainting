package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class CloneSubCommand extends ConcreteCreateSubCommand{
	{ description = "@create.clone.description"; paramList = "<cloned>"; }
	
	public static final String CLONED_UNSPECIFIED = "clonedUnspecified";
	public String clonedUnspecified = "@create.clone.clonedUnspecified";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@create.clone.canvasNotExists";
	
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
			MapCanvasRegistry entry = painting.getCanvas(name, sender);
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
