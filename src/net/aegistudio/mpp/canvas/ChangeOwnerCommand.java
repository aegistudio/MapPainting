package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;

public class ChangeOwnerCommand extends ActualHandle implements HazardCommand {
	{	description = "@chown.description"; 	}
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = "@chown.onlyPlayer";
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@chown.canvasNotExists";
	
	public static final String OWNERSHIP_CHANGED = "ownershipChanged";
	public String ownershipChanged = "@chown.ownershipChanged";
	
	public static final String OWNERSHIP_GAINED = "ownershipGained";
	public String ownershipGained = "@chown.ownershipGained";
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = "@chown.notHolding";
	
	public static final String NO_CHOWN_PERMISSION = "noChownPermission";
	public String noChownPermission = "@chown.noChownPermission";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.ownershipChanged = painting.getLocale(OWNERSHIP_CHANGED, ownershipChanged, section);
		this.ownershipGained = painting.getLocale(OWNERSHIP_GAINED, ownershipGained, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.noChownPermission = painting.getLocale(NO_CHOWN_PERMISSION, noChownPermission, section);
	}
	
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
			registry = painting.canvas.holding(player);
			if(registry == null) {
				sender.sendMessage(notHolding);
				return true;
			}
		}
		else {
			registry = painting.getCanvas(arguments[1], sender);
			if(registry == null) {
				sender.sendMessage(canvasNotExists.replace("$canvasName", arguments[1]));
				return true;
			}
		}
		
		if(!registry.hasPermission(sender, "chown")) {
			sender.sendMessage(noChownPermission.replace("$canvasName", registry.name));
			return true;
		}
		
		ChownState state = new ChownState();
		state.newOwner = arguments[0];
		state.registry = registry;
		
		painting.hazard.hazard(sender, this, state);
		return true;
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
		painting.ackHistory(state.registry, sender);
		
		Player newOwner = painting.getServer().getPlayer(state.newOwner);
		if(newOwner != null) newOwner.sendMessage(ownershipGained
				.replace("$canvasName", state.registry.name));
	}
}
