package net.aegistudio.mpp.canvas;

import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;

public class CreateCanvasCommand extends CompositeHandle {
	{ description = "@create.description"; }
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = "@create.onlyPlayer";
	
	public static final String NO_CREATE_PERMISSION = "noCreatePermission";
	public String noCreatePermission = "@create.noCreatePermission";
	
	public static final String NOT_HOLDING_MAP = "notHoldingMap";
	public String notHoldingMap = "@create.notHoldingMap";
	
	public static final String MAP_ALREADY_BOUND = "mapAlreadyBound";
	public String mapAlreadyBound = "@create.mapAlreadyBound";
	
	public static final String TOO_MANY_MAP = "tooManyMap";
	public String tooManyMap = "@create.tooManyMap";
	
	public static final String CANVAS_ALREADY_EXISTED = "canvasAlreadyExisted";
	public String canvasAlreadyExisted = "@create.canvasAlreadyExisted";
	
	public static final String BOUND = "bound";
	public String bound = "@create.bound";
	
	public static final String PROMPT_CONFIRM = "promptConfirm";
	public boolean promptConfirm = false;
	
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
		
		if(!section.contains(PROMPT_CONFIRM)) section.set(PROMPT_CONFIRM, promptConfirm);
		else promptConfirm = section.getBoolean(PROMPT_CONFIRM);
	}
}
