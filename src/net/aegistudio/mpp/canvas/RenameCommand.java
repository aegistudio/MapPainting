package net.aegistudio.mpp.canvas;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class RenameCommand extends ActualHandle {
	{	description = "Rename an existing canvas."; 	}

	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use rename command without specifying old canvas name!";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot rename canvas " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RED + "! Specified canvas " + ChatColor.AQUA + "$canvasName"
			+ ChatColor.RED + " doesn't exist!";

	public static final String CANVAS_ALREADY_EXIST = "canvasAlreadyExist";
	public String canvasAlreadyExist = ChatColor.RED + "Cannot rename canvas to " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RED + "! Canvas of name " + ChatColor.AQUA + "$canvasName"
			+ ChatColor.RED + " already exists!";
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = ChatColor.RED + "You should either hold a canvas in hand or specify the canvas name!";
	
	public static final String NO_RENAME_PERMISSION = "noRenamePermission";
	public String noRenamePermission = ChatColor.RED + "You don't have permission to rename " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public static final String SUCCESSFULLY_RENAME = "successfullyRename";
	public String successfullyRename = ChatColor.WHITE + "You have successfully rename " + ChatColor.AQUA + "$oldname"
			+ ChatColor.RESET + " to " + ChatColor.AQUA + "$newname" + ChatColor.RESET + "!";
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) 
			sender.sendMessage(prefix + " [<oldname>] <newname>");
		else {
			MapCanvasRegistry oldcanvas;
			String newname;
			if(arguments.length == 1) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(onlyPlayer);
					return true;
				}
				
				Player player = (Player) sender;
				oldcanvas = painting.canvas.holding(player);
				if(oldcanvas == null) {
					sender.sendMessage(notHolding);
					return true;
				}
				
				newname = arguments[0];
			}
			else {
				if(null == (oldcanvas = painting.canvas.nameCanvasMap.get(arguments[0])) || oldcanvas.removed()) {
					sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[0]));
					return true;
				}
				newname = arguments[1];
			}
			
			if(!oldcanvas.hasPermission(sender, "rename")) {
				sender.sendMessage(noRenamePermission);
				return true;
			}
			
			if(painting.canvas.nameCanvasMap.containsKey(newname)) {
				sender.sendMessage(canvasAlreadyExist.replace("$canvasName", newname));
				return true;
			}
			else {
				String oldname = oldcanvas.name;
				painting.canvas.nameCanvasMap.put(newname, oldcanvas);
				oldcanvas.name = newname;
				painting.canvas.nameCanvasMap.remove(oldname);
				
				sender.sendMessage(successfullyRename.replace("$oldname", oldname)
						.replace("$newname", newname));
				
				if(sender instanceof Player) {
					Player player = (Player) sender;
					painting.canvas.scopeListener.make(player.getItemInHand(), oldcanvas);
				}
			}
		}
		return true;
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.canvasAlreadyExist = painting.getLocale(CANVAS_ALREADY_EXIST, canvasAlreadyExist, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.noRenamePermission = painting.getLocale(NO_RENAME_PERMISSION, noRenamePermission, section);
		this.successfullyRename = painting.getLocale(SUCCESSFULLY_RENAME, successfullyRename, section);
	}
}
