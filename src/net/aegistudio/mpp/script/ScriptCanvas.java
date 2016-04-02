package net.aegistudio.mpp.script;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.Graphic;

/**
 * Oh. I have nothing to say.
 * 
 * @author aegistudio
 */

public class ScriptCanvas extends Canvas {
	public ScriptCanvas(MapPainting painting) {
		super(painting);
	}

	public String filename;
	
	public Graphic graphic = new Graphic(this);
	public Callback callback = new Callback();
	public TreeMap<String, Object> cassette = new TreeMap<String, Object>();
	
	public static ScriptEngineFactory factory;
	public ScriptEngine engine;
	
	protected void tick() {
		super.tick();
		callback.tickTrigger();
	}
	
	public void setEngine() throws UnsupportedException{
		if(this.engine == null) {
			String language = filename.substring(filename.lastIndexOf('.') + 1);
			ScriptEngineFactory factory = ScriptEnginePool.factories.get(language);
			if(factory == null) throw new UnsupportedException(language);
			this.engine = factory.getScriptEngine();
		}
	}
	
	public void setScript() throws Exception {
		Bindings binding = engine.createBindings();
		// Load basics.
		binding.put("graphic", this.graphic); binding.put("g", this.graphic);
		binding.put("callback", this.callback); binding.put("i", this.callback);
		binding.put("casette", this.cassette); binding.put("c", this.cassette);
		
		// Load algorithms.
		binding.put("algorihtm", this.painting.algorithm);
		binding.put("a", this.painting.algorithm);
		
		// Load others.
		binding.put("plugin", this.painting);
		binding.put("server", this.painting.getServer());
		
		engine.setBindings(binding, ScriptContext.ENGINE_SCOPE);
		
		FileReader reader = new FileReader(new File(painting.getDataFolder(), filename));
		
		if(engine instanceof Compilable)
			((Compilable) engine).compile(reader).eval();
		else engine.eval(reader);
		
		callback.setScript((Invocable)engine);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(MapPainting painting, InputStream mppFile) throws Exception {
		GZIPInputStream gzip = new GZIPInputStream(mppFile);
		DataInputStream din = new DataInputStream(gzip);
		
		filename = din.readUTF();
		this.setEngine();
		
		graphic.read(din);
		callback.read(din);
		cassette = (TreeMap<String, Object>) Token.COMPOSITE.parse(din, engine);
		
		this.setScript();
	}
	
	@Override
	public void save(MapPainting painting, OutputStream mppFile) throws Exception {
		GZIPOutputStream gzip = new GZIPOutputStream(mppFile);
		DataOutputStream dout = new DataOutputStream(gzip);
		
		dout.writeUTF(filename);
		graphic.write(dout);
		callback.write(dout);
		Token.COMPOSITE.persist(dout, engine, cassette);
		
		gzip.finish();
		gzip.flush();
	}

	public void reboot(String[] arguments) throws Exception {
		((Invocable)engine).invokeFunction("main", (Object[])arguments);
	}
	
	@Override
	public void paint(Interaction interact, Color color) {			}

	@Override
	public Color look(int x, int y) {
		return null;
	}

	@Override
	public boolean interact(Interaction interact) {
		this.callback.tapTrigger(interact.x, interact.y, interact.sender, interact.rightHanded);
		return true;
	}

	@Override
	public int size() {
		return 128;
	}

	@Override
	public Canvas clone() {
		return null;
	}
	
	@Override
	protected void subrender(MapView view, MapCanvas canvas, Player player) {
		this.graphic.subrender(view, canvas, player);
	}
}
