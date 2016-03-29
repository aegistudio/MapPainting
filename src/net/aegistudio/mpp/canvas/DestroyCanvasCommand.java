package net.aegistudio.mpp.canvas;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;

public class DestroyCanvasCommand extends ActualHandle implements HazardCommand {
	{ description = "Destroy a canvas, making bound map normal."; }
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot destroy " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String NO_PERMISSION = "noPermission";
	public String noPermission = ChatColor.RED + "You are neither the owner of the painting " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + " nor the manager!";
	
	public static final String UNBOUND = "unbound";
	public String unbound = "The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + " has been unbound.";
	
	public static final String HOLDING = "holding";
	public String holding = "You're holding the canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET 
			+ " in your hand. Please confirm if you want to destroy it, or just use " 
			+ ChatColor.YELLOW + "$prefix <name>" + ChatColor.RESET + " to specify a map.";
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 1) {
			MapCanvasRegistry canvas = null;
			if(sender instanceof Player) {
				Player player = (Player)sender;
				ItemStack item = player.getItemInHand();
				if(item.getType() == Material.MAP)
					canvas = painting.canvas.idCanvasMap.get(item.getDurability());
			}
			
			if(canvas != null) 
				if(this.hasPermission(sender, canvas)) {
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

			if(!this.hasPermission(sender, canvas)) {
				sender.sendMessage(noPermission.replace("$canvasName", arguments[0]));
				return true;
			}
			
			painting.hazard.hazard(sender, this, canvas);
		}
		return true;
	}
	
	public boolean hasPermission(CommandSender sender, MapCanvasRegistry canvas) {
		if(sender.hasPermission("mpp.manager")) return true;
		if(sender.hasPermission("mpp.destroy"))
			if(canvas.owner.equals(sender.getName())) return true;
		return false;
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		noPermission = painting.getLocale(NO_PERMISSION, noPermission, section);
		unbound = painting.getLocale(UNBOUND, unbound, section);
	}
	
	@Override
	public void handle(MapPainting painting, CommandSender sender, Object hazardState) {
		MapCanvasRegistry canvas = (MapCanvasRegistry) hazardState;
		painting.canvas.remove(canvas);
		
		sender.sendMessage(unbound.replace("$canvasName", canvas.name));
	}
}
