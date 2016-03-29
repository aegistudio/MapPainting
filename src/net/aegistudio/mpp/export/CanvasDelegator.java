package net.aegistudio.mpp.export;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.CanvasMppInputStream;
import net.aegistudio.mpp.canvas.Graphic;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

/**
 * The delegator has blocked direct access from
 * plugin canvas to map painting itself.
 * 
 * @author aegistudio
 */

public class CanvasDelegator<T extends PluginCanvas> extends Canvas implements Peripheral {
	
	public Graphic graphic;
	public CanvasDelegator(MapPainting painting) {
		super(painting);
		this.graphic = new Graphic(this);
	}

	private MapCanvasRegistry registry;
	public void add(MapCanvasRegistry registry) {
		this.registry = registry;
		painting.foreign.plugin(plugin).place(this);
	}
	
	public MapCanvasRegistry getRegistry() {
		return this.registry;
	}
	
	public String plugin;
	public String identifier;
	public T canvasInstance;
	
	public void create(PluginCanvasFactory<T> factory) {
		if(canvasInstance == null) try {
			canvasInstance = factory.create(this);
			File file = new File(painting.getDataFolder(), registry.name.concat(".mpp"));
			try(FileInputStream input = new FileInputStream(file);
				CanvasMppInputStream mppInput = new CanvasMppInputStream(input);) {
				mppInput.readHeader();
				mppInput.readClass();
				
				canvasInstance.load(input);
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
		
		if(this.canvasInstance != null) 
			canvasInstance.load(din);
	}
	
	@Override
	public void save(MapPainting painting, OutputStream mppFile) throws Exception {
		GZIPOutputStream gzip = new GZIPOutputStream(mppFile);
		DataOutputStream dout = new DataOutputStream(gzip);
		
		dout.writeUTF(plugin);
		dout.writeUTF(identifier);
		graphic.write(dout);
		
		if(this.canvasInstance != null)
			canvasInstance.save(dout);
		
		gzip.finish();
		gzip.flush();
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
		return canvasInstance.interact(interact);
	}

	@Override
	public int size() {
		return graphic.size();
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
	protected void subrender(MapView view, MapCanvas canvas, Player player) {
		graphic.subrender(view, canvas, player);
	}

	@Override
	public Graphic getGraphic() {
		return graphic;
	}
}
