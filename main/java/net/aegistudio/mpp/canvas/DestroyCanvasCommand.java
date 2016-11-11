package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;

public class DestroyCanvasCommand extends ActualHandle implements HazardCommand {
	{ description = "@destroy.description"; }
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@destroy.canvasNotExists";
	
	public static final String NO_PERMISSION = "noPermission";
	public String noPermission = "@destroy.noDestroyPermission";
	
	public static final String UNBOUND = "unbound";
	public String unbound = "@destroy.unbound";
	
	public static final String HOLDING = "holding";
	public String holding = "@destroy.holding";
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 1) {
			MapCanvasRegistry canvas = null;
			if(sender instanceof Player) 
				canvas = painting.canvas.holding((Player) sender);
			
			if(canvas != null) 
				if(canvas.hasPermission(sender, "destroy")) {
					sender.sendMessage(holding
							.replace("$canvasName", canvas.name)
							.replace("$prefix", prefix));
					painting.hazard.hazard(sender, this, canvas);
					return true;
				}
			sender.sendMessage(prefix + " <name>");
		}
		else {
			MapCanvasRegistry canvas = painting.getCanvas(arguments[0], sender);
			if(canvas == null) { 
				sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[0]));
				return true;
			}

			if(!canvas.hasPermission(sender, "destroy")) {
				sender.sendMessage(noPermission.replace("$canvasName", arguments[0]));
				return true;
			}
			
			painting.hazard.hazard(sender, this, canvas);
		}
		return true;
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		noPermission = painting.getLocale(NO_PERMISSION, noPermission, section);
		holding = painting.getLocale(HOLDING, holding, section);
		unbound = painting.getLocale(UNBOUND, unbound, section);
	}
	
	@Override
	public void handle(MapPainting painting, CommandSender sender, Object hazardState) {
		MapCanvasRegistry canvas = (MapCanvasRegistry) hazardState;
		painting.canvas.remove(canvas);
		
		sender.sendMessage(unbound.replace("$canvasName", canvas.name));
	}
}
