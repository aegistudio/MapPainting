/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Item
 *  org.bukkit.map.MapView
 */
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
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.Paintable;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.Graphic;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.export.Context;
import net.aegistudio.mpp.export.PlaceSensitive;
import net.aegistudio.mpp.export.PluginCanvas;
import net.aegistudio.mpp.export.PluginCanvasFactory;
import net.aegistudio.mpp.export.PluginCanvasRegistry;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.map.MapView;

public class CanvasDelegator<T extends PluginCanvas>
extends Canvas
implements Context,
PluginCanvasRegistry<T> {
    public Graphic graphic = new Graphic(this);
    private MapCanvasRegistry registry;
    public String plugin;
    public String identifier;
    public T canvasInstance;
    PluginCanvasFactory<T> factory;

    public CanvasDelegator(MapPainting painting) {
        super(painting);
    }

    @Override
    public void add(MapCanvasRegistry registry) {
        super.add(registry);
        this.registry = registry;
        this.painting.m_foreignCanvasManager.plugin(this.plugin).place(this);
        if (this.canvasInstance != null) {
            this.canvasInstance.add(this);
        }
    }

    @Override
    public void remove(MapCanvasRegistry registry) {
        super.remove(registry);
        this.painting.m_foreignCanvasManager.plugin(this.plugin).watchlist(this.identifier).remove(this);
        if (this.canvasInstance != null) {
            this.canvasInstance.remove(this);
        }
    }

    public MapCanvasRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public int mapid() {
        if (this.registry == null) {
            return -1;
        }
        if (this.registry.removed()) {
            return -1;
        }
        return this.registry.binding;
    }

    @Override
    public String name() {
        if (this.registry == null) {
            return null;
        }
        if (this.registry.removed()) {
            return null;
        }
        return this.registry.name;
    }

    @Override
    public String plugin() {
        return this.plugin;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public T canvas() {
        return this.canvasInstance;
    }

    @Override
    public PluginCanvasFactory<T> factory() {
        return this.factory;
    }

    public void create(PluginCanvasFactory<T> factory) {
        if (this.factory != factory) {
            try {
                if (this.canvasInstance != null) {
                    this.canvasInstance.remove(this);
                }
                this.canvasInstance = factory.create(this);
                this.factory = factory;
                this.loadCanvasInstance();
                this.canvasInstance.add(this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadCanvasInstance() {
        if (this.canvasInstance != null) {
            try {
                File file = new File(this.painting.getDataFolder(), this.registry.name.concat(".dat"));
                if (!file.exists()) {
                    return;
                }
                try (FileInputStream input = new FileInputStream(file);
                     GZIPInputStream gzip = new GZIPInputStream(input);){
                    this.canvasInstance.load(gzip);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveCanvasInstance() {
        if (this.canvasInstance != null) {
            try {
                File file = new File(this.painting.getDataFolder(), this.registry.name.concat(".dat"));
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileOutputStream output = new FileOutputStream(file);
                     GZIPOutputStream gzip = new GZIPOutputStream(output);){
                    this.canvasInstance.save(gzip);
                    gzip.finish();
                    gzip.flush();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void load(MapPainting painting, InputStream mppFile) throws Exception {
        GZIPInputStream gzip = new GZIPInputStream(mppFile);
        DataInputStream din = new DataInputStream(gzip);
        this.plugin = din.readUTF();
        this.identifier = din.readUTF();
        this.graphic.read(din);
        this.loadCanvasInstance();
    }

    @Override
    public void save(MapPainting painting, OutputStream mppFile) throws Exception {
        GZIPOutputStream gzip = new GZIPOutputStream(mppFile);
        DataOutputStream dout = new DataOutputStream(gzip);
        dout.writeUTF(this.plugin);
        dout.writeUTF(this.identifier);
        this.graphic.write(dout);
        gzip.finish();
        gzip.flush();
        this.saveCanvasInstance();
    }

    @Override
    public void paint(Interaction interact, Color color) {
        if (this.canvasInstance != null) {
            this.canvasInstance.paint(interact, color);
        }
    }

    @Override
    public Color look(int x, int y) {
        return this.graphic.get(x, y);
    }

    @Override
    public boolean interact(Interaction interact) {
        if (this.canvasInstance == null) {
            return false;
        }
        return this.canvasInstance.interact(interact);
    }

    @Override
    public int size() {
        return 128;
    }

    @Override
    public Canvas clone() {
        CanvasDelegator<T> cloned = new CanvasDelegator<T>(this.painting);
        if (this.canvasInstance != null) {
            cloned.canvasInstance = this.canvasInstance;
        }
        cloned.graphic = new Graphic(cloned);
        cloned.graphic.copy(this.graphic);
        cloned.identifier = this.identifier;
        cloned.plugin = this.plugin;
        return cloned;
    }

    @Override
    protected void subrender(MapView view, Paintable canvas) {
        this.graphic.subrender(view, canvas);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.canvasInstance != null) {
            this.canvasInstance.tick();
        }
    }

    @Override
    public void bcolor(byte c) {
        this.graphic.bcolor(c);
    }

    @Override
    public void color(Color c) {
        this.graphic.color(c);
    }

    @Override
    public int width() {
        return this.graphic.width();
    }

    @Override
    public int height() {
        return this.graphic.height();
    }

    @Override
    public void set(int x, int y) {
        this.graphic.set(x, y);
    }

    @Override
    public Color get(int x, int y) {
        return this.graphic.get(x, y);
    }

    @Override
    public byte bget(int x, int y) {
        return this.graphic.bget(x, y);
    }

    @Override
    public void clear() {
        this.graphic.clear();
    }

    @Override
    public void place(Location blockLocation, BlockFace blockFace) {
        if (this.canvasInstance != null && this.canvasInstance instanceof PlaceSensitive) {
            ((PlaceSensitive)this.canvasInstance).place(blockLocation, blockFace);
        }
    }

    @Override
    public void unplace(Item spawnedItem) {
        if (this.canvasInstance != null && this.canvasInstance instanceof PlaceSensitive) {
            ((PlaceSensitive)this.canvasInstance).unplace(spawnedItem);
        }
    }
}

