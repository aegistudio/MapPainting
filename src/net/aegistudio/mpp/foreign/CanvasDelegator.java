package net.aegistudio.mpp.foreign;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.Paintable;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.Graphic;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.export.Context;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCanvasFactory;
import net.aegistudio.mpp.export.PluginCanvasRegistry;

/**
 * The delegator has blocked direct access from
 * plugin canvas to map painting itself.
 * 
 * @author aegistudio
 */

public class CanvasDelegator<T extends PluginCanvas> extends Canvas implements Context, PluginCanvasRegistry<T> {
	
	public Graphic graphic;
	public CanvasDelegator(MapPainting painting) {
		super(painting);
		this.graphic = new Graphic(this);
	}

	private MapCanvasRegistry registry;
	public void add(MapCanvasRegistry registry) {
		super.add(registry);
		this.registry = registry;
		painting.foreignCanvas.plugin(plugin).place(this);
		if(this.canvasInstance != null)
			this.canvasInstance.add(this);
	}
	
	public void remove(MapCanvasRegistry registry) {
		super.remove(registry);
		painting.foreignCanvas.plugin(plugin).watchlist(identifier).remove(this);
		if(canvasInstance != null) canvasInstance.remove(this);
	}
	
	public MapCanvasRegistry getRegistry() {
		return this.registry;
	}

	@Override
	public int mapid() {
		if(this.registry == null) return -1;
		if(this.registry.removed()) return -1;
		return this.registry.binding;
	}

	@Override
	public String name() {
		if(this.registry == null) return null;
		if(this.registry.removed()) return null;
		return this.registry.name;
	}

	public String plugin;
	@Override
	public String plugin() {
		return plugin;
	}

	public String identifier;
	@Override
	public String identifier() {
		return identifier;
	}

	public T canvasInstance;
	@Override
	public T canvas() {
		return canvasInstance;
	}
	
	PluginCanvasFactory<T> factory;
	@Override
	public PluginCanvasFactory<T> factory() {
		return factory;
	}
	
	public void create(PluginCanvasFactory<T> factory) {
		if(this.factory != factory) try {
			if(canvasInstance != null)
				canvasInstance.remove(this);
			
			canvasInstance = factory.create(this);
			this.factory = factory;
			
			this.loadCanvasInstance();
			canvasInstance.add(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadCanvasInstance() {
		if(canvasInstance != null) try {
			File file = new File(painting.getDataFolder(), registry.name.concat(".dat"));
			if(!file.exists()) return;
			try(FileInputStream input = new FileInputStream(file);
				GZIPInputStream gzip = new GZIPInputStream(input);) {
				
				canvasInstance.load(gzip);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveCanvasInstance() {
		if(canvasInstance != null) try {
			File file = new File(painting.getDataFolder(), registry.name.concat(".dat"));
			if(!file.exists()) file.createNewFile();
			
			try(FileOutputStream output = new FileOutputStream(file);
				GZIPOutputStream gzip = new GZIPOutputStream(output);) {
			
				canvasInstance.save(gzip);

				gzip.finish();
				gzip.flush();	
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void load(MapPainting painting, InputStream mppFile) throws Exception {
		GZIPInputStream gzip = new GZIPInputStream(mppFile);
		DataInputStream din = new DataInputStream(gzip);
		
		plugin = din.readUTF();
		identifier = din.readUTF();
		graphic.read(din);
		
		this.loadCanvasInstance();
	}
	
	@Override
	public void save(MapPainting painting, OutputStream mppFile) throws Exception {
		GZIPOutputStream gzip = new GZIPOutputStream(mppFile);
		DataOutputStream dout = new DataOutputStream(gzip);
		
		dout.writeUTF(plugin);
		dout.writeUTF(identifier);
		graphic.write(dout);
		
		gzip.finish();
		gzip.flush();
		
		this.saveCanvasInstance();
	}

	@Override
	public void paint(Interaction interact, Color color) {
		if(canvasInstance != null)
			canvasInstance.paint(interact, color);
	}

	@Override
	public Color look(int x, int y) {
		return graphic.get(x, y);
	}

	@Override
	public boolean interact(Interaction interact) {
		if(canvasInstance == null) return false;
		return canvasInstance.interact(interact);
	}

	@Override
	public int size() {
		return 128;
	}

	@Override
	public Canvas clone() {
		CanvasDelegator<T> cloned = new CanvasDelegator<T>(painting);
		if(this.canvasInstance != null)
			cloned.canvasInstance = this.canvasInstance;
		cloned.graphic = new Graphic(cloned);
		cloned.graphic.copy(graphic);
		cloned.identifier = this.identifier;
		cloned.plugin = this.plugin;
		return cloned;
	}

	@Override
	protected void subrender(MapView view, Paintable canvas) {
		graphic.subrender(view, canvas);
	}
	
	@Override
	public void tick() {
		super.tick();
		if(this.canvasInstance != null)
			this.canvasInstance.tick();
	}

	@Override
	public void bcolor(byte c) {
		graphic.bcolor(c);
	}

	@Override
	public void color(Color c) {
		graphic.color(c);
	}

	@Override
	public int width() {
		return graphic.width();
	}

	@Override
	public int height() {
		return graphic.height();
	}

	@Override
	public void set(int x, int y) {
		graphic.set(x, y);
	}

	@Override
	public Color get(int x, int y) {
		return graphic.get(x, y);
	}

	@Override
	public byte bget(int x, int y) {
		return graphic.bget(x, y);
	}

	@Override
	public void clear() {
		graphic.clear();
	}
}
