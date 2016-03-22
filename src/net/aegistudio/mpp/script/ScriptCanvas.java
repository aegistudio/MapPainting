package net.aegistudio.mpp.script;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;

/**
 * Oh. I have nothing to say.
 * 
 * @author aegistudio
 */

public class ScriptCanvas extends Canvas {
	public ScriptCanvas(MapPainting painting) {
		super(painting);
	}

	public Graphic graphic = new Graphic(this);
	public Callback callback = new Callback();
	public Cassette casette = new Cassette();
	
	public static ScriptEngineFactory factory;
	public ScriptEngine engine;
	
	public void setScript(String filename) throws Exception {
		if(factory == null) {
			for(ScriptEngineFactory sf : new ScriptEngineManager().getEngineFactories())
				if(sf.getLanguageName().equals("ECMAScript")) {
					factory = sf;
					break;
				}
		}
		this.engine = factory.getScriptEngine();
		
		Bindings binding = engine.createBindings();
		binding.put("graphic", this.graphic); binding.put("g", this.graphic);
		binding.put("callback", this.callback); binding.put("i", this.callback);
		binding.put("casette", this.casette); binding.put("c", this.casette);
		
		binding.put("plugin", this.painting);
		binding.put("server", this.painting.getServer());
		engine.setBindings(binding, ScriptContext.ENGINE_SCOPE);
		engine.eval(new FileReader(new File(painting.getDataFolder(), filename)));
		
		callback.setScript((Invocable)engine);
	}

	@Override
	public void load(MapPainting painting, InputStream mppFile) throws Exception {
		
	}
	
	@Override
	public void save(MapPainting painting, OutputStream mppFile) throws Exception {
		
	}

	public void reboot() throws Exception {
		((Invocable)engine).invokeFunction("main");
	}
	
	@Override
	public void paint(Interaction interact, Color color) {			}

	@Override
	public Color look(int x, int y) {
		return null;
	}

	@Override
	public boolean interact(Interaction interact) {
		this.callback.tapTrigger(interact.x, interact.y, interact.sender);
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

	public void render(MapView view, MapCanvas canvas, Player player) {
		this.callback.tickTrigger();
		super.render(view, canvas, player);
	}
	
	@Override
	protected void subrender(MapView view, MapCanvas canvas, Player player) {
		this.graphic.subrender(view, canvas, player);
	}
}
