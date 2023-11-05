/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class ListCanvasCommand
extends ActualHandle {
    public static final String NO_POSSESS = "noPossess";
    public String noPossess;
    public static final String ENTRY = "entry";
    public String entry;
    public static final String POSSESS = "possess";
    public String possess;

    public ListCanvasCommand() {
        this.description = "@list.description";
        this.noPossess = "@list.noPossess";
        this.entry = "@list.entry";
        this.possess = "@list.possess";
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.noPossess = painting.getLocale(NO_POSSESS, this.noPossess, section);
        this.entry = painting.getLocale(ENTRY, this.entry, section);
        this.possess = painting.getLocale(POSSESS, this.possess, section);
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        StringBuilder list = new StringBuilder();
        boolean first = true;
        for (MapCanvasRegistry canvas : painting.m_canvasManager.nameCanvasMap.values()) {
        
        	// check if filter argument has been supplied
        	if (arguments.length >= 1) {
        		if (!canvas.name.toLowerCase().contains(arguments[0].toLowerCase())) continue;
        	}
        	
        	if (!canvas.owner.equals(sender.getName())) continue;
            
            // if this is first loop skip adding seperator
            if (first) {
                first = false;
            } else {
                list.append(", ");
            }
            
            // add the canvas to list
            list.append(this.entry.replace("$name", canvas.name));
            
        }
        
        if (list.length() > 0) {
            sender.sendMessage(this.possess.replace("$list", new String(list)));
        } else {
            sender.sendMessage(this.noPossess);
        }
        return true;
    }
}

