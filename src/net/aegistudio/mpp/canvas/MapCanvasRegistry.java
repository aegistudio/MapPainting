/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.map.MapRenderer
 *  org.bukkit.map.MapView
 *  org.bukkit.permissions.Permissible
 */
package net.aegistudio.mpp.canvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import net.aegistudio.mpp.History;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permissible;

public class MapCanvasRegistry
implements Module {
    public String name;
    public Integer binding;
    public MapView view;
    public Canvas canvas;
    public String owner;
    public TreeSet<String> painter;
    public TreeSet<String> interactor;
    public History history;
    public static final String BINDING = "id";
    public static final String OWNER = "owner";
    public static final String PAINTER = "painter";
    public static final String INTERACTOR = "interactor";
    private boolean removed = false;

    public MapCanvasRegistry(String name) {
        this.name = name;
        this.painter = new TreeSet();
        this.interactor = new TreeSet();
        this.interactor.add("#all");
        this.history = new History();
    }

    public boolean select(TreeSet<String> set, String who, Permissible sender) {
        if (set.contains(who)) {
            return true;
        }
        if (set.contains("#-" + who)) {
            return false;
        }
        if (set.contains("#reject:" + who)) {
            return false;
        }
        if (set.contains("#all")) {
            return true;
        }
        if (sender == null) {
            return false;
        }
        if (set.contains("#op") && sender.isOp()) {
            return true;
        }
        for (String permissionEntry : set.tailSet("#perm:", false)) {
            if (!permissionEntry.startsWith("#perm:")) break;
            if (!sender.hasPermission(permissionEntry.substring("#perm:".length()))) continue;
            return true;
        }
        return false;
    }

    public boolean canPaint(CommandSender sender) {
        return this.select(this.painter, sender.getName(), (Permissible)sender);
    }

    public boolean canInteract(CommandSender sender) {
        return this.select(this.interactor, sender.getName(), (Permissible)sender);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission("mpp.manager")) {
            return true;
        }
        if (!sender.hasPermission("mpp." + permission)) {
            return false;
        }
        return this.owner.equals(sender.getName());
    }

    @Override
    public void load(MapPainting plugin, ConfigurationSection canvas) throws Exception {
    	
    	// the binding ID associated with the map?
    	this.binding = (int)canvas.getInt(BINDING);
    	
    	plugin.getServer().getConsoleSender().sendMessage("attempting to bind " + binding.toString());
    	
    	// the actual mapview (display surface?)
        this.view = plugin.getServer().getMap(this.binding);
        
        this.owner = canvas.getString(OWNER);
        
        this.painter = new TreeSet(canvas.getStringList(PAINTER));
        
        if (canvas.contains(INTERACTOR)) {
            this.interactor = new TreeSet(canvas.getStringList(INTERACTOR));
        } else {
            this.interactor = new TreeSet();
            this.interactor.add("#all");
        }
        
        File file = new File(plugin.getDataFolder(), this.name.concat(".mpp"));
        
        try (FileInputStream input = new FileInputStream(file);
             CanvasMppInputStream cin = new CanvasMppInputStream(input);){
            this.canvas = cin.readCanvas(plugin);
        }
        
        
    }

    @Override
    public void save(MapPainting plugin, ConfigurationSection canvas) throws Exception {
        if (this.removed()) {
            return;
        }
        canvas.set(BINDING, (Object)this.binding);
        canvas.set(OWNER, (Object)this.owner);
        canvas.set(PAINTER, new ArrayList<String>(this.painter));
        if (this.interactor.contains("#all") && this.interactor.size() == 1) {
            canvas.set(INTERACTOR, null);
        } else {
            canvas.set(INTERACTOR, new ArrayList<String>(this.painter));
        }
        File file = new File(plugin.getDataFolder(), this.name.concat(".mpp"));
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream output = new FileOutputStream(file);
             CanvasMppOutputStream cout = new CanvasMppOutputStream(output)){
            cout.writeCanvas(plugin, this.canvas);
        }
        this.removeNoMark();
    }

    public void add() {
        this.view.addRenderer((MapRenderer)this.canvas);
        this.canvas.add(this);
    }

    public void remove() {
        this.removeNoMark();
        this.removed = true;
    }

    public void removeNoMark() {
        this.view.removeRenderer((MapRenderer)this.canvas);
        this.canvas.remove(this);
    }

    public boolean removed() {
        return this.removed;
    }
}

