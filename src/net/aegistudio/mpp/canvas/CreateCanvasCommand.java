/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.CompositeHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.configuration.ConfigurationSection;

public class CreateCanvasCommand
extends CompositeHandle {
    public static final String ONLY_PLAYER = "onlyPlayer";
    public String onlyPlayer;
    public static final String NO_CREATE_PERMISSION = "noCreatePermission";
    public String noCreatePermission;
    public static final String NOT_HOLDING_MAP = "notHoldingMap";
    public String notHoldingMap;
    public static final String MAP_ALREADY_BOUND = "mapAlreadyBound";
    public String mapAlreadyBound;
    public static final String TOO_MANY_MAP = "tooManyMap";
    public String tooManyMap;
    public static final String CANVAS_ALREADY_EXISTED = "canvasAlreadyExisted";
    public String canvasAlreadyExisted;
    public static final String BOUND = "bound";
    public String bound;
    public static final String PROMPT_CONFIRM = "promptConfirm";
    public boolean promptConfirm;
    public static final String CANT_AFFORD = "cantAfford";
    public String cantAfford;

    public CreateCanvasCommand() {
        this.description = "@create.description";
        this.onlyPlayer = "@create.onlyPlayer";
        this.noCreatePermission = "@create.noCreatePermission";
        this.notHoldingMap = "@create.notHoldingMap";
        this.mapAlreadyBound = "@create.mapAlreadyBound";
        this.tooManyMap = "@create.tooManyMap";
        this.canvasAlreadyExisted = "@create.canvasAlreadyExisted";
        this.bound = "@create.bound";
        this.cantAfford = "@create.cantAfford";
        this.promptConfirm = false;
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.onlyPlayer = painting.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
        this.noCreatePermission = painting.getLocale(NO_CREATE_PERMISSION, this.noCreatePermission, section);
        this.notHoldingMap = painting.getLocale(NOT_HOLDING_MAP, this.notHoldingMap, section);
        this.tooManyMap = painting.getLocale(TOO_MANY_MAP, this.tooManyMap, section);
        this.mapAlreadyBound = painting.getLocale(MAP_ALREADY_BOUND, this.mapAlreadyBound, section);
        this.canvasAlreadyExisted = painting.getLocale(CANVAS_ALREADY_EXISTED, this.canvasAlreadyExisted, section);
        this.bound = painting.getLocale(BOUND, this.bound, section);
        this.cantAfford = painting.getLocale(CANT_AFFORD, this.cantAfford, section);
        if (!section.contains(PROMPT_CONFIRM)) {
            section.set(PROMPT_CONFIRM, (Object)this.promptConfirm);
        } else {
            this.promptConfirm = section.getBoolean(PROMPT_CONFIRM);
        }
    }
}

