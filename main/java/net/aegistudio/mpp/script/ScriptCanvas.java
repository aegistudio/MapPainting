/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.map.MapView
 */
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
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.Paintable;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.Graphic;
import org.bukkit.map.MapView;

public class ScriptCanvas
extends Canvas {
    public String filename;
    public Graphic graphic = new Graphic(this);
    public Callback callback = new Callback();
    public TreeMap<String, Object> cassette = new TreeMap();
    public static ScriptEngineFactory factory;
    public ScriptEngine engine;

    public ScriptCanvas(MapPainting painting) {
        super(painting);
    }

    @Override
    protected void tick() {
        super.tick();
        this.callback.tickTrigger();
    }

    public void setEngine() throws UnsupportedException {
        if (this.engine == null) {
            String language = this.filename.substring(this.filename.lastIndexOf(46) + 1);
            ScriptEngineFactory factory = ScriptEnginePool.factories.get(language);
            if (factory == null) {
                throw new UnsupportedException(language);
            }
            this.engine = factory.getScriptEngine();
        }
    }

    public void setScript() throws Exception {
        Bindings binding = this.engine.createBindings();
        binding.put("graphic", (Object)this.graphic);
        binding.put("g", (Object)this.graphic);
        binding.put("callback", (Object)this.callback);
        binding.put("i", (Object)this.callback);
        binding.put("casette", this.cassette);
        binding.put("c", this.cassette);
        binding.put("asset", (Object)this.painting.m_assetManager);
        binding.put("a", (Object)this.painting.m_assetManager);
        binding.put("plugin", (Object)this.painting);
        binding.put("server", (Object)this.painting.getServer());
        this.engine.setBindings(binding, 100);
        FileReader reader = new FileReader(new File(this.painting.getDataFolder(), this.filename));
        if (this.engine instanceof Compilable) {
            ((Compilable)((Object)this.engine)).compile(reader).eval();
        } else {
            this.engine.eval(reader);
        }
        this.callback.setScript((Invocable)((Object)this.engine));
    }

    @Override
    public void load(MapPainting painting, InputStream mppFile) throws Exception {
        GZIPInputStream gzip = new GZIPInputStream(mppFile);
        DataInputStream din = new DataInputStream(gzip);
        this.filename = din.readUTF();
        this.setEngine();
        this.graphic.read(din);
        this.callback.read(din);
        this.cassette = (TreeMap)Token.COMPOSITE.parse(din, this.engine);
        this.setScript();
    }

    @Override
    public void save(MapPainting painting, OutputStream mppFile) throws Exception {
        GZIPOutputStream gzip = new GZIPOutputStream(mppFile);
        DataOutputStream dout = new DataOutputStream(gzip);
        dout.writeUTF(this.filename);
        this.graphic.write(dout);
        this.callback.write(dout);
        Token.COMPOSITE.persist(dout, this.engine, this.cassette);
        gzip.finish();
        gzip.flush();
    }

    public void reboot(String[] arguments) throws Exception {
        ((Invocable)((Object)this.engine)).invokeFunction("main", arguments);
    }

    @Override
    public void paint(Interaction interact, Color color) {
    }

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
    protected void subrender(MapView view, Paintable canvas) {
        this.graphic.subrender(view, canvas);
    }
}

