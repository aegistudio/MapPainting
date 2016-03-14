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

public class ChangeOwnerCommand extends ActualHandle implements HazardCommand {
	{	description = "Transfer the ownership of a canvas."; 	}
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use tranfer ownership command without specifying canvas name!";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot transfer ownership of " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RED + "! Specified canvas " + ChatColor.AQUA + "$canvasName"
			+ ChatColor.RED + " doesn't exist!";
	
	public static final String OWNERSHIP_CHANGED = "ownershipChanged";
	public String ownershipChanged = "You have successfully transfer the ownership of " + ChatColor.AQUA 
			+ "$canvasName" + ChatColor.RESET + " to " + ChatColor.BLUE + "$newOwner" + ChatColor.RESET + "!";
	
	public static final String OWNERSHIP_GAINED = "ownershipGained";
	public String ownershipGained = "You have just gained the ownership of " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = ChatColor.RED + "You should either hold a canvas in hand or specify the canvas name!";
	
	public static final String NO_CHOWN_PERMISSION = "noChownPermission";
	public String noChownPermission = ChatColor.RED + "You don't have permission to transfer ownership of " 
			+ ChatColor.AQUA + "$canvasName" + ChatColor.RED + "!";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = this.getLocale(ONLY_PLAYER, onlyPlayer, section);
		this.canvasNotExists = this.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.ownershipChanged = this.getLocale(OWNERSHIP_CHANGED, ownershipChanged, section);
		this.ownershipGained = this.getLocale(OWNERSHIP_GAINED, ownershipGained, section);
		this.notHolding = this.getLocale(NOT_HOLDING, notHolding, section);
		this.noChownPermission = this.getLocale(NO_CHOWN_PERMISSION, noChownPermission, section);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage(prefix + " <receiver> [<name>]");
			return true;
		}
		
		MapCanvasRegistry registry;
		if(arguments.length <= 1) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayer);
				return true;
			}
			Player player = (Player) sender;
			ItemStack item = player.getItemInHand();
			
			if(item.getType() != Material.MAP ||
					null == (registry = painting.canvas.idCanvasMap.get(item.getDurability()))) {
				sender.sendMessage(notHolding);
				return true;
			}
		}
		else {
			registry = painting.canvas.nameCanvasMap.get(arguments[1]);
			if(registry == null) {
				sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[1]));
				return true;
			}
		}
		
		if(!hasPermission(sender, registry)) {
			sender.sendMessage(noChownPermission.replace("$canvasName", registry.name));
			return true;
		}
		
		ChownState state = new ChownState();
		state.newOwner = arguments[0];
		state.registry = registry;
		
		painting.hazard.hazard(sender, this, state);
		return true;
	}

	public boolean hasPermission(CommandSender sender, MapCanvasRegistry canvas) {
		if(sender.hasPermission("mpp.manager")) return true;
		if(!sender.hasPermission("mpp.chown")) return false;
		if(canvas.owner.equals(sender.getName())) return true;
		return false;
	}

	class ChownState {
		public String newOwner;
		public MapCanvasRegistry registry;
	}
	
	@Override
	public void handle(MapPainting painting, CommandSender sender, Object hazardState) {
		ChownState state = (ChownState) hazardState;
		state.registry.owner = state.newOwner;
		state.registry.painter.add(state.newOwner);
		
		sender.sendMessage(ownershipChanged
				.replace("$canvasName", state.registry.name)
				.replace("$newOwner", state.newOwner));
		Player newOwner = painting.getServer().getPlayer(state.newOwner);
		if(newOwner != null) newOwner.sendMessage(ownershipGained
				.replace("$canvasName", state.registry.name));
	}
}
