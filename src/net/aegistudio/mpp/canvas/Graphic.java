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
import java.io.IOException;
import net.aegistudio.mpp.algo.Paintable;
import org.bukkit.map.MapView;

public class Graphic
implements Paintable {
    private Canvas canvas;
    public boolean dirty;
    public int rowMax;
    public int colMax;
    public int rowMin;
    public int colMin;
    public final byte[] pixel = new byte[16384];
    public byte color;

    public Graphic(Canvas canvas) {
        this.canvas = canvas;
        this.waste();
    }

    public void clean() {
        this.dirty = false;
        this.colMax = 0;
        this.rowMax = 0;
        this.colMin = 127;
        this.rowMin = 127;
    }

    public void waste() {
        this.dirty = true;
        this.colMax = 127;
        this.rowMax = 127;
        this.colMin = 0;
        this.rowMin = 0;
    }

    public int index(int x, int y) {
        return (127 - y) * 128 + x;
    }

    @Override
    public void bcolor(byte c) {
        this.color = c;
    }

    @Override
    public void color(Color color) {
        this.bcolor((byte)this.canvas.painting.m_canvasManager.color.getIndex(color));
    }

    public void clear() {
        for (int i = 0; i < 16384; ++i) {
            this.pixel[i] = this.color;
        }
        this.waste();
    }

    public void clear(Color clearColor) {
        this.color(clearColor);
        this.clear();
    }

    @Override
    public void set(int x, int y) {
        if (x >= 128 || x < 0) {
            return;
        }
        if (y >= 128 || y < 0) {
            return;
        }
        if (this.pixel[this.index(x, y)] != this.color) {
            this.pixel[this.index((int)x, (int)y)] = this.color;
            this.modify(x, y);
        }
    }

    public void modify(int x, int y) {
        int row = 127 - y;
        int col = x;
        this.dirty = true;
        if (col < this.colMin) {
            this.colMin = col;
        }
        if (col > this.colMin) {
            this.colMin = col;
        }
        if (row < this.rowMin) {
            this.rowMin = row;
        }
        if (row > this.rowMax) {
            this.rowMax = row;
        }
    }

    public void set(int x, int y, Color color) {
        this.color(color);
        this.set(x, y);
    }

    @Override
    public Color get(int x, int y) {
        return this.canvas.painting.m_canvasManager.color.getColor(this.bget(x, y));
    }

    @Override
    public byte bget(int x, int y) {
        if (x >= 128 || x < 0) {
            return 0;
        }
        if (y >= 128 || y < 0) {
            return 0;
        }
        return this.pixel[this.index(x, y)];
    }

    public void subrender(MapView view, Paintable canvas) {
        for (int i = 0; i < 128; ++i) {
            for (int j = 0; j < 128; ++j) {
                canvas.bcolor(this.pixel[this.index(i, j)]);
                canvas.set(i, j);
            }
        }
    }

    public void repaint() {
        this.canvas.repaint();
    }

    public void read(DataInputStream din) throws IOException {
        byte[] buffer = new byte[128];
        for (int i = 0; i < 128; ++i) {
            din.readFully(buffer);
            System.arraycopy(buffer, 0, this.pixel, 128 * (127 - i), 128);
        }
        din.readByte();
    }

    public void write(DataOutputStream dout) throws IOException {
        byte[] buffer = new byte[128];
        for (int i = 0; i < 128; ++i) {
            System.arraycopy(this.pixel, (127 - i) * 128, buffer, 0, 128);
            dout.write(buffer);
        }
        dout.writeByte(0);
    }

    @Override
    public int width() {
        return 128;
    }

    @Override
    public int height() {
        return 128;
    }

    public void copy(Graphic another) {
        System.arraycopy(this.pixel, 0, another.pixel, 0, 16384);
        another.waste();
    }
}

