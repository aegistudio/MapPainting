package net.aegistudio.mpp.control;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;

public class ControlCommand extends CompositeHandle {
	{ description = "control canvases through a line of command."; }
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = ChatColor.RED + "Cannot control " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + 
			"! Specified canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " doesn't exist!";
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = ChatColor.RED + "A input argument is not in right format! Please check your arguments!";

	public static final String INVALID_ARGUMENTS = "invalidArguments";
	public String invalidArguments = ChatColor.RED + "Too more or too less arguments are specified! Please check your arguments!";
	
	public static final String NO_CONTROL_PERMISSION = "noControlPermission";
	public String noControlPermission = ChatColor.RED + "You don't have control permission on canvas " + ChatColor.AQUA + 
			"$canvasName" + ChatColor.RED + "!";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
	}
}
