package net.aegistudio.mpp.control;

import java.util.TreeSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.CanvasWrapper;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class WrapControlCommand extends ConcreteControlCommand {
	{	description = "change the wrapped canvas of a wrapper.";	paramList = "<newWrapped> [<layer>]";  }
	public static final String CANNOT_WRAP = "canontWrap";
	public String cannotWrap = ChatColor.RED + "Cannot wrap canvas " + ChatColor.AQUA + "$canvasName"
			+ ChatColor.RED + "! The wrapper will wrap itself!";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.cannotWrap = painting.getLocale(CANNOT_WRAP, cannotWrap, section);
	}
	
	@Override
	protected void subhandle(MapPainting painting, MapCanvasRegistry canvas, CommandSender sender, String[] arguments) {
		if(!sender.hasPermission("mpp.control.wrap")) {
			sender.sendMessage(painting.control.noControlPermission.replace("$canvasName", canvas.name));
			return;
		}
		
		int layer = 0;
		if(arguments.length < 1 || arguments.length > 2) {
			sender.sendMessage(painting.control.invalidArguments);
			return;
		}
		
		if(arguments.length == 2) try {
			layer = Integer.parseInt(arguments[1]);
		}
		catch(Throwable throwable) {
			sender.sendMessage(painting.control.invalidFormat);
			return;
		}
		
		if(!(canvas.canvas instanceof CanvasWrapper)) {
			sender.sendMessage(painting.control.mismatchedType.replace("$canvasName", canvas.name)
					.replace("$canvasType", "wrapper"));
			return;
		}
		else {
			if(arguments[0].equals(canvas.name)) {
				sender.sendMessage(cannotWrap.replace("$canvasName", canvas.name));
				return;
			}
			
			MapCanvasRegistry target = painting.canvas.nameCanvasMap.get(arguments[0]);
			if(target != null && (target.canvas instanceof CanvasWrapper)) {
				TreeSet<String> wrapping = new TreeSet<String>();
				((CanvasWrapper)(target.canvas)).showWrapped(wrapping);
				if(wrapping.contains(canvas.name)) {
					sender.sendMessage(cannotWrap.replace("$canvasName", canvas.name));
					return;
				}
			}
			((CanvasWrapper)(canvas.canvas)).setWrapping(layer, arguments[0]);
			painting.ackHistory(canvas, sender);
		}
	}
}