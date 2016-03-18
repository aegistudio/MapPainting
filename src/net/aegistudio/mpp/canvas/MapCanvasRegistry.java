package net.aegistudio.mpp.canvas;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.History;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class MapCanvasRegistry implements Module {
	public final String name;
	public short binding;
	public MapView view;
	public Canvas canvas;
	public String owner;
	public TreeSet<String> painter;
	public History history;
	
	public MapCanvasRegistry(String name) {
		this.name = name;
		this.painter = new TreeSet<String>();
		this.history = new History();
	}
	
	public static final String BINDING = "id";
	public static final String OWNER = "owner";
	public static final String PAINTER = "painter";
	
	@SuppressWarnings("deprecation")
	public void load(MapPainting map, ConfigurationSection canvas) throws Exception {
		binding = (short) canvas.getInt(BINDING);
		view = map.getServer().getMap(binding);
		this.owner = canvas.getString(OWNER);
		this.painter = new TreeSet<String>(canvas.getStringList(PAINTER));
		
		File file = new File(map.getDataFolder(), name.concat(".mpp"));
		try(FileInputStream input = new FileInputStream(file);
			DataInputStream din = new DataInputStream(input);) {
			
			if(din.readByte() != 'P') throw new Exception("Wrecked file!");
			if(din.readByte() != 'P') throw new Exception("Wrecked file!");
			if(din.readByte() != 'M') throw new Exception("Wrecked file!");
			
			String canvasClass = din.readUTF();
			this.canvas = (Canvas) Class.forName(canvasClass).newInstance();
			this.canvas.load(map, this, input);
		}
		this.add();
	}
	
	public void save(MapPainting map, ConfigurationSection canvas) throws Exception{
		canvas.set(BINDING, binding);
		canvas.set(OWNER, owner);
		canvas.set(PAINTER, new ArrayList<String>(this.painter));
		
		File file = new File(map.getDataFolder(), name.concat(".mpp"));
		if(!file.exists()) file.createNewFile();
		try(FileOutputStream output = new FileOutputStream(file);
			DataOutputStream dout = new DataOutputStream(output);) {
		
			dout.writeByte('P');
			dout.writeByte('P');
			dout.writeByte('M');
			dout.writeUTF(this.canvas.getClass().getName());
			dout.flush();
			
			this.canvas.save(map, this, output);
		}
		this.remove();
	}
	
	public void add() {
		view.addRenderer(this.canvas.getRenderer());
	}
	
	public void remove() {
		view.removeRenderer(this.canvas.getRenderer());
	}
}
