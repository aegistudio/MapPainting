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
	
	public static final String NOT_HOLDING_MAP = "notHoldingMap";
	public String notHoldingMap = "You're not holding a map in your hand. "
			+ "Confirm if you would like to create a new one, or just ignore it if you won't.";
	
	public static final String MAP_ALREADY_BOUND = "mapAlreadyBound";
	public String mapAlreadyBound = "The map you're holding has already been bound to a map. " 
			+ "You could create a new one by confirming, or just ignore it if you won't.";
	
	public static final String TOO_MANY_MAP = "tooManyMap";
	public String tooManyMap = ChatColor.RED + "It seems you have created too many map that no new map could be created!";
	
	public static final String CANVAS_ALREADY_EXISTED = "canvasAlreadyExisted";
	public String canvasAlreadyExisted = ChatColor.RED + "The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " has already existed!";
	
	public static final String BOUND = "bound";
	public String bound = "You have successfully bound " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		noCreatePermission = painting.getLocale(NO_CREATE_PERMISSION, noCreatePermission, section);
		notHoldingMap = painting.getLocale(NOT_HOLDING_MAP, notHoldingMap, section);
		tooManyMap = painting.getLocale(TOO_MANY_MAP, tooManyMap, section);
		mapAlreadyBound = painting.getLocale(MAP_ALREADY_BOUND, mapAlreadyBound, section);
		canvasAlreadyExisted = painting.getLocale(CANVAS_ALREADY_EXISTED, canvasAlreadyExisted, section);
		bound = painting.getLocale(BOUND, bound, section);
	}
}
