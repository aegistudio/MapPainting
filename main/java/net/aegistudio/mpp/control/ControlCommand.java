package net.aegistudio.mpp.control;

import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;

public class ControlCommand extends CompositeHandle {
	{ description = "@control.description"; }
	
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists = "@control.canvasNotExists";
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = "@control.invalidFormat";

	public static final String INVALID_ARGUMENTS = "invalidArguments";
	public String invalidArguments = "@control.invalidArguments";
	
	public static final String NO_CONTROL_PERMISSION = "noControlPermission";
	public String noControlPermission = "@control.noControlPermission";
	
	public static final String MISMATCHED_TYPE = "mismatchedType";
	public String mismatchedType = "@control.mismatchedType";
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, canvasNotExists, section);
		this.invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
		this.invalidArguments = painting.getLocale(INVALID_ARGUMENTS, invalidArguments, section);
		this.noControlPermission = painting.getLocale(NO_CONTROL_PERMISSION, noControlPermission, section);
		this.mismatchedType = painting.getLocale(MISMATCHED_TYPE, mismatchedType, section);
	}
}
