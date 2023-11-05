/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package net.aegistudio.mpp.tool;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.PaintTool;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.paint.PaintManager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Pencil
implements PaintTool {
    public TreeMap<Integer, PencilTickCounter> lastStroke = new TreeMap();
    public MapPainting plugin;
    long interval = 1L;
    int initCount = 7;
    public static final String TAP_MESSAGE = "tapMessage";
    public String tapMessage = "@memento.tap";
    public static final String LINE_MESSAGE = "lineMessage";
    public String lineMessage = "@memento.line";

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        this.plugin = painting;
        painting.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)painting, new Runnable(){

            @Override
            public void run() {
                Iterator<Map.Entry<Integer, PencilTickCounter>> counter = Pencil.this.lastStroke.entrySet().iterator();
                while (counter.hasNext()) {
                    Map.Entry<Integer, PencilTickCounter> current = counter.next();
                    --current.getValue().count;
                    if (current.getValue().count > 0) continue;
                    counter.remove();
                }
            }
        }, this.interval, this.interval);
        this.tapMessage = painting.getLocale(TAP_MESSAGE, this.tapMessage, section);
        this.lineMessage = painting.getLocale(LINE_MESSAGE, this.lineMessage, section);
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }

    @Override
    public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
        if (itemStack.getType() == Material.POTION) {
            Color color = plugin.m_paintManager.getItemColor(itemStack);
            this.pencilPaint(interact, canvas, color);
            return true;
        }
        return false;
    }
    
    public void toggleVisible() {
    	
    }

    protected void pencilPaint(Interaction interact, MapCanvasRegistry canvas, Color color) {
        Integer entityId = this.getTickCounterKey(interact);
        PencilTickCounter last = this.lastStroke.get(entityId);
        if (last != null && last.canvas == canvas) {
            canvas.history.add(new LineDrawingMemento(this.plugin, canvas.canvas, last.interaction.x, last.interaction.y, interact.x, interact.y, color, this.lineMessage, interact));
        } else {
            canvas.history.add(new PixelTapMemento(canvas.canvas, interact, color, this.tapMessage));
        }
        if (entityId != null) {
            this.lastStroke.put(entityId, new PencilTickCounter(interact, canvas, this.initCount));
        }
    }

    protected Integer getTickCounterKey(Interaction interact) {
        if (interact.sender != null && interact.sender instanceof Player) {
            return ((Player)interact.sender).getEntityId();
        }
        return null;
    }

}

