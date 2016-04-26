package net.aegistudio.mpp.canvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.History;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class MapCanvasRegistry implements Module {
	public String name;
	public short binding;
	public MapView view;
	public Canvas canvas;
	public String owner;
	public TreeSet<String> painter;
	public TreeSet<String> interactor;
	public History history;
	
	public MapCanvasRegistry(String name) {
		this.name = name;
		this.painter = new TreeSet<String>();
		this.history = new History();
	}
	
	public boolean select(TreeSet<String> set, CommandSender sender) {
		String who = sender.getName();
		if(set.contains(who)) return true;
		if(set.contains("#-" + who)) return false;
		if(set.contains("#reject:" + who)) return false;
		if(set.contains("#all")) return true;
		if(set.contains("#op")) if(sender.isOp()) return true;
		for(String permissionEntry : set.tailSet("#perm:", false)) {
			if(!permissionEntry.startsWith("#perm:")) break;
			if(sender.hasPermission(permissionEntry.substring("#perm:".length())))
				return true;
		}
		return false;
	}
	
	public boolean canPaint(CommandSender sender) {
		return select(painter, sender);
	}
	
	public boolean canInteract(CommandSender sender) {
		return select(interactor, sender);
	}
	
	public boolean hasPermission(CommandSender sender, String permission) {
		if(sender.hasPermission("mpp.manager")) return true;
		if(!sender.hasPermission("mpp." + permission)) return false;
		if(owner.equals(sender.getName())) return true;
		return false;
	}
	
	public static final String BINDING = "id";
	public static final String OWNER = "owner";
	public static final String PAINTER = "painter";
	public static final String INTERACTOR = "interactor";
	
	@SuppressWarnings("deprecation")
	public void load(MapPainting map, ConfigurationSection canvas) throws Exception {
		binding = (short) canvas.getInt(BINDING);
		view = map.getServer().getMap(binding);
		this.owner = canvas.getString(OWNER);
		
		// Read painters.
		this.painter = new TreeSet<String>(canvas.getStringList(PAINTER));
		
		// Read interactors.
		if(canvas.contains(INTERACTOR))
			this.interactor = new TreeSet<String>(canvas.getStringList(INTERACTOR));
		else {
			this.interactor = new TreeSet<String>();
			this.interactor.add("#all");
		}
		
		File file = new File(map.getDataFolder(), name.concat(".mpp"));
		try(FileInputStream input = new FileInputStream(file);
		CanvasMppInputStream cin = new CanvasMppInputStream(input);) {
			this.canvas = cin.readCanvas(map);
		}
	}
	
	public void save(MapPainting map, ConfigurationSection canvas) throws Exception {
		if(removed()) return;
		canvas.set(BINDING, binding);
		canvas.set(OWNER, owner);
		
		// Write painters.
		canvas.set(PAINTER, new ArrayList<String>(this.painter));
		
		// Writer interactors.
		if(interactor.contains("#all") && interactor.size() == 1)
			canvas.set(INTERACTOR, null);
		else canvas.set(INTERACTOR, new ArrayList<String>(this.painter));
		
		File file = new File(map.getDataFolder(), name.concat(".mpp"));
		if(!file.exists()) file.createNewFile();
		try(FileOutputStream output = new FileOutputStream(file);
		CanvasMppOutputStream cout = new CanvasMppOutputStream(output);) {
			cout.writeCanvas(map, this.canvas);
		}
		this.remove();
	}
	
	public void add() {
		view.addRenderer(this.canvas);
		this.canvas.add(this);
	}
	
	private boolean removed = false;
	public void remove() {
		view.removeRenderer(this.canvas);
		this.canvas.remove(this);
		this.removed = true;
	}
	
	public boolean removed() {
		return removed;
	}
}
