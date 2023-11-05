/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapView
 */
package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.Paintable;

import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class BufferedCanvas
extends Canvas {
    public byte[][] pixel;
    public int size;

    public BufferedCanvas(MapPainting painting) {
        super(painting);
    }

    @Override
    public BufferedCanvas clone() {
        BufferedCanvas canvas = new BufferedCanvas(this.painting);
        canvas.size = this.size;
        canvas.pixel = new byte[this.size][this.size];
        for (int i = 0; i < this.size; ++i) {
            System.arraycopy(this.pixel[i], 0, canvas.pixel[i], 0, this.size);
        }
        return canvas;
    }

    @Override
    public void subrender(MapView view, Paintable canvas) {
        for (int i = 0; i < 128; ++i) {
            for (int j = 0; j < 128; ++j) {
                canvas.bcolor(this.pixel[(int)(1.0 * (double)i / 128.0 * (double)this.size)][(int)(1.0 * (double)j / 128.0 * (double)this.size)]);
                canvas.set(i, 127 - j);
            }
        }
    }

    
    /**
     * The most common update location for paint clicks on common canvas objects. 
     * Changes the color of a pixel on the canvas based on the interaction properties.
     * @param interact - The change details being implemented
     * @param color - The new pixel color
     */
    @Override
    public void paint(Interaction interact, Color color) {
    	
    	// Guard against the interaction point being outside the canvas bounds
        int x = interact.x;
        if (x >= this.size || x < 0) {
            return;
        }
        int y = this.size - 1 - interact.y;
        if (y >= this.size || y < 0) {
            return;
        }
        
        // Store the current pixel color so we don't update it unless the color is changing
        byte precolor = this.pixel[x][y];
        
        // Assign the new color byte, translating null color to transparent
        byte colorByte = color == null ? (byte)0 : (byte)this.painting.m_canvasManager.color.getIndex(color);
        this.pixel[x][y] = colorByte;
        
        // If the color has actually changed, refresh the image
        if (this.pixel[x][y] != precolor) {
            this.repaint();
        }
    }

    @Override
    public Color look(int x, int y) {
        if (x >= this.size || x < 0) {
            return null;
        }
        if ((y = this.size - 1 - y) >= this.size || y < 0) {
            return null;
        }
        if (this.pixel[x][y] == 0) {
            return null;
        }
        return this.painting.m_canvasManager.color.getColor(this.pixel[x][y]);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void load(MapPainting painting, InputStream file) throws Exception {
        DataInputStream din = new DataInputStream(file);
        this.size = din.readShort();
        this.pixel = new byte[this.size][this.size];
        GZIPInputStream input = new GZIPInputStream(file);
        for (int i = 0; i < this.size; ++i) {
            int next;
            for (int j = 0; j < this.size && (next = input.read()) != -1; ++j) {
                this.pixel[i][j] = (byte)next;
            }
        }
    }

    @Override
    public void save(MapPainting painting, OutputStream file) throws Exception {
        DataOutputStream dout = new DataOutputStream(file);
        dout.writeShort(this.size);
        GZIPOutputStream output = new GZIPOutputStream(file);
        for (int i = 0; i < this.size; ++i) {
            output.write(this.pixel[i], 0, this.size);
        }
        output.finish();
        output.flush();
    }

    @Override
    public boolean interact(Interaction info) {
        return false;
    }
}

