/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.map.MapCanvas
 *  org.bukkit.map.MapRenderer
 *  org.bukkit.map.MapView
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.MapCanvasAdapter;
import net.aegistudio.mpp.algo.Paintable;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Canvas
extends MapRenderer
implements Cloneable {
    public final MapPainting painting;
    private Graphic context = new Graphic(this);
    protected BukkitRunnable tickRunnable = new BukkitRunnable(){

        public void run() {
            Canvas.this.tick();
        }
    };
    protected MapView view;
    protected final TreeSet<Integer> viewed = new TreeSet();
    protected final HashSet<Object> observers = new HashSet();
    protected final HashMap<Player, Integer> suspector = new HashMap();
    public boolean updateDisplay = true;

    public Canvas(MapPainting painting) {
        this.painting = painting;
    }

    public abstract void load(MapPainting var1, InputStream var2) throws Exception;

    public abstract void paint(Interaction var1, Color var2);

    public abstract Color look(int var1, int var2);

    public abstract boolean interact(Interaction var1);

    public abstract int size();

    public abstract void save(MapPainting var1, OutputStream var2) throws Exception;

    public abstract Canvas clone();

    public void place(Location blockLocation, BlockFace face) {
    }

    public void unplace(Item dropped) {
    }

    protected void tick() {
    	
    	// TODO - Fix slow packet updates, version specific here
    	
        if (this.updateDisplay) {
            this.subrender(this.view, this.context);
            this.updateDisplay = false;
        }
        
        //PacketPlayOutMap mapPacket = null;
        
        /*
        
        //ProtocolManager pm = ProtocolLibrary.getProtocolManager();        
        PacketContainer packet = pm.createPacket(PacketType.Play.Server.MAP);
        packet.getModifier().writeDefaults();
        
        Boolean trackingPosition = false;
        Boolean mapLocked = true;
        
        packet.getBooleans()
        	.write(0, trackingPosition)
        	.write(1, mapLocked);
        
        pm.sendServerPacket(player, packet);
        
        */
        
        // ????? NO IDEA IF THIS IS AT ALL VALID
        
        // BOOLEAN 0 - TRACKING POSITION
        // BOOLEAN 1 - LOCKED
        // BYTES 0 - SET SCALE
         // MODIFIER 4 - MAP ICONS
         // INT 0 - ITEM DAMAGE
         // INT 1 - COLUMNS
         // INT 2 - ROWS;
         // INT 3 - X
         // INT 4 - Y
         // BYTEARRAY 0 - MAP DATA        
        
        // https://wiki.vg/Protocol#Map_Data
        
        /*
         * 	private int a;
		    private byte b;
		    private boolean c;
		    private boolean d;
		    private MapIcon[] e;
		    private int f;
		    private int g;
		    private int h;
		    private int i;
		    private byte[] j;
         */
        
        
       

        /*
         * 
         * CODE STOLEN FROM ELSEWHERE. EXAMPLE ONLY
        
        packet.getDoubles().
            write(0, player.getLocation().getX()).
            write(1, player.getLocation().getY()).
            write(2, player.getLocation().getZ());
        packet.getFloat().write(0, 3.0F);

        try {
            pm.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                "Cannot send packet " + packet, e);
        }
        
        */
        
        
        
        
        if (this.context.dirty) {
            //mapPacket = new PacketPlayOutMap(this.painting.inject, this.view, this.context.pixel, 0, this.context.rowMin, 128, this.context.rowMax - this.context.rowMin + 1);
        }
        Iterator<Map.Entry<Player, Integer>> suspector = this.suspector.entrySet().iterator();
        while (suspector.hasNext()) {
            Map.Entry<Player, Integer> entry = suspector.next();
            entry.setValue(entry.getValue() + 1);
            if (entry.getValue() >= this.painting.m_canvasManager.suspectTimedOut) {
                suspector.remove();
                continue;
            }
            //if(packet == null) continue;
            
            // TODO - why is inject dead?
            /*
            try {
            	//pm.sendServerPacket(TODO - PLAYER, packet);
            	int test = 5;
            	// empty
            } catch (InvocationTargetException e) {
            	throw new RuntimeException(
                        "Cannot send packet " + packet, e);
            }
            */
            
            //new PlayerConnection(this.painting.inject, entry.getKey()).sendPacket(mapPacket);
        }
        this.context.clean();
    }

    public void add(MapCanvasRegistry registry) {
        this.tickRunnable.runTaskTimer((Plugin)this.painting, 1L, 1L);
        this.view = registry.view;
    }

    public void remove(MapCanvasRegistry registry) {
        this.tickRunnable.cancel();
    }

    public void render(MapView view, MapCanvas canvas, Player player) {
        if (!this.hasViewed(player)) {
            this.context.subrender(view, new MapCanvasAdapter(this.painting.m_canvasManager.color, canvas));
        }
        this.suspector.put(player, 0);
    }

    protected abstract void subrender(MapView var1, Paintable var2);

    public synchronized void repaint() {
        this.viewed.clear();
        this.observers.clear();
        this.updateDisplay = true;
    }

    public synchronized boolean hasViewed(Player player) {
        if (player == null) {
            return false;
        }
        if (this.viewed.contains(player.getEntityId())) {
            return true;
        }
        this.viewed.add(player.getEntityId());
        return false;
    }

    public synchronized boolean hasObserver(Object observer) {
        if (this.observers.contains(observer)) {
            return true;
        }
        this.observers.add(observer);
        return false;
    }

}

