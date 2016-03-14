package net.aegistudio.mpp.canvas;

import java.util.ArrayList;
import java.util.Objects;
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
		this.painter = new TreeSet<>();
	}
	
	public static final String BINDING = "id";
	public static final String TYPE = "type";
	public static final String OWNER = "owner";
	public static final String PAINTER = "painter";
	
	@SuppressWarnings("deprecation")
	@Override
	public void load(MapPainting map, ConfigurationSection canvas) throws Exception {
		binding = (short) canvas.getInt(BINDING);
		view = map.getServer().getMap(binding);
		this.canvas = EnumCanvasFactory.getFactory(canvas.getString(TYPE)).create();
		this.canvas.load(map, this, canvas);
		this.owner = canvas.getString(OWNER);
		this.painter = new TreeSet<>(canvas.getStringList(PAINTER));
		
		this.add();
	}
	
	@Override
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MapCanvasRegistry other = (MapCanvasRegistry) obj;
		if (this.binding != other.binding) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return Objects.equals(this.owner, other.owner);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Objects.hashCode(this.name);
		hash = 97 * hash + this.binding;
		hash = 97 * hash + Objects.hashCode(this.owner);
		return hash;
	}
}
