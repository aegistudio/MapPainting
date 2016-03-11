package net.aegistudio.mpp.canvas;

import java.util.ArrayList;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class MapCanvasRegistry implements Module {
	public final String name;
	public short binding;
	public MapView view;
	public Canvas canvas;
	public String owner;
	public TreeSet<String> painter;
	
	public MapCanvasRegistry(String name) {
		this.name = name;
		this.painter = new TreeSet<String>();
	}
	
	public static final String BINDING = "id";
	public static final String TYPE = "type";
	public static final String OWNER = "owner";
	public static final String PAINTER = "painter";
	
	@SuppressWarnings("deprecation")
	public void load(MapPainting map, ConfigurationSection canvas) throws Exception {
		binding = (short) canvas.getInt(BINDING);
		view = map.getServer().getMap(binding);
		this.canvas = EnumCanvasFactory.getFactory(canvas.getString(TYPE)).create();
		this.canvas.load(map, this, canvas);
		this.owner = canvas.getString(OWNER);
		this.painter = new TreeSet<String>(canvas.getStringList(PAINTER));
		
		this.add();
	}
	
	public void save(MapPainting map, ConfigurationSection canvas) throws Exception{
		canvas.set(BINDING, binding);
		canvas.set(TYPE, EnumCanvasFactory.getFactory(this.canvas));
		canvas.set(OWNER, owner);
		canvas.set(PAINTER, new ArrayList<String>(this.painter));
		this.canvas.save(map, this, canvas);
		
		view.removeRenderer(this.canvas.getRenderer());
		this.remove();
	}
	
	public void add() {
		view.addRenderer(this.canvas.getRenderer());
	}
	
	public void remove() {
		view.removeRenderer(this.canvas.getRenderer());
	}
}
