package net.aegistudio.mpp.canvas;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.ChatColor;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;

public class CreateCanvasCommand extends CompositeHandle {
	{ description = "Create a new canvas based on arguments."; }
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use create canvas command.";
	
	public static final String NO_CREATE_PERMISSION = "noCreatePermission";
	public String noCreatePermission = ChatColor.RED + "You don't have permission to create canvas of this type.";
	
	public static final String SHOULD_HOLD_MAP = "shouldHoldMap";
	public String shouldHoldMap = ChatColor.RED + "You should hold a map in your hand to turn it into canvas.";
	
	public static final String MAP_ALREADY_BOUND = "mapAlreadyBound";
	public String mapAlreadyBound = ChatColor.RED + "The map you're holding has already been bound to a map.";
	
	public static final String CANVAS_ALREADY_EXISTED = "canvasAlreadyExisted";
	public String canvasAlreadyExisted = ChatColor.RED + "The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " has already existed!";
	
	public static final String BOUND = "bound";
	public String bound = "You have successfully bound " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		noCreatePermission = painting.getLocale(NO_CREATE_PERMISSION, noCreatePermission, section);
		shouldHoldMap = painting.getLocale(SHOULD_HOLD_MAP, shouldHoldMap, section);
		mapAlreadyBound = painting.getLocale(MAP_ALREADY_BOUND, mapAlreadyBound, section);
		canvasAlreadyExisted = painting.getLocale(CANVAS_ALREADY_EXISTED, canvasAlreadyExisted, section);
		bound = painting.getLocale(BOUND, bound, section);
	}
}
