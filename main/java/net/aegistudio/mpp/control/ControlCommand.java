/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.control;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.configuration.ConfigurationSection;

public class ControlCommand
extends CompositeHandle {
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String INVALID_FORMAT = "invalidFormat";
    public String invalidFormat;
    public static final String INVALID_ARGUMENTS = "invalidArguments";
    public String invalidArguments;
    public static final String NO_CONTROL_PERMISSION = "noControlPermission";
    public String noControlPermission;
    public static final String MISMATCHED_TYPE = "mismatchedType";
    public String mismatchedType;

    public ControlCommand() {
        this.description = "@control.description";
        this.canvasNotExists = "@control.canvasNotExists";
        this.invalidFormat = "@control.invalidFormat";
        this.invalidArguments = "@control.invalidArguments";
        this.noControlPermission = "@control.noControlPermission";
        this.mismatchedType = "@control.mismatchedType";
    }

    @Override
    public void load(MapPainting plugin, ConfigurationSection config) throws Exception {
        super.load(plugin, config);
        this.canvasNotExists = plugin.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, config);
        this.invalidFormat = plugin.getLocale(INVALID_FORMAT, this.invalidFormat, config);
        this.invalidArguments = plugin.getLocale(INVALID_ARGUMENTS, this.invalidArguments, config);
        this.noControlPermission = plugin.getLocale(NO_CONTROL_PERMISSION, this.noControlPermission, config);
        this.mismatchedType = plugin.getLocale(MISMATCHED_TYPE, this.mismatchedType, config);
    }
}

