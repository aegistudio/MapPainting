/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp.script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.script.Invocable;
import org.bukkit.command.CommandSender;

public class SubCallback {
    public TreeMap<String, Region> hotspot = new TreeMap();
    public TreeSet<String> tick = new TreeSet();
    public TreeSet<String> exit = new TreeSet();
    public TreeSet<String> entry = new TreeSet();

    public void remove(String target) {
        this.hotspot.remove(target);
        this.tick.remove(target);
        this.exit.remove(target);
        this.entry.remove(target);
    }

    public void hotspot(String handle, int x1, int y1, int x2, int y2) {
        this.hotspot.put(handle, new Region(x1, y1, x2, y2));
    }

    public void tick(String handle) {
        this.tick.add(handle);
    }

    public void exit(String handle) {
        this.exit.add(handle);
    }

    public void entry(String handle) {
        this.entry.add(handle);
    }

    void exitTrigger(Invocable script, String prestate, String poststate) {
        for (String _exit : this.exit) {
            try {
                script.invokeFunction(_exit, prestate, poststate);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    void entryTrigger(Invocable script, String prestate, String poststate) {
        for (String _entry : this.entry) {
            try {
                script.invokeFunction(_entry, prestate, poststate);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    void tapTrigger(Invocable script, int x, int y, CommandSender who, boolean rightHanded) {
        if (script != null) {
            for (Map.Entry<String, Region> tap : this.hotspot.entrySet()) {
                try {
                    if (!tap.getValue().inside(x, y)) continue;
                    script.invokeFunction(tap.getKey(), new Object[]{x, y, who, rightHanded});
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    void tickTrigger(Invocable script) {
        if (script != null) {
            for (String _tick : this.tick) {
                try {
                    script.invokeFunction(_tick, new Object[0]);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public void read(DataInputStream input) throws IOException {
        this.hotspot.clear();
        this.tick.clear();
        this.exit.clear();
        this.entry.clear();
        int hotspots = input.readInt();
        for (int i = 0; i < hotspots; ++i) {
            String name = input.readUTF();
            int x1 = input.readInt();
            int y1 = input.readInt();
            int x2 = input.readInt();
            int y2 = input.readInt();
            Region region = new Region(x1, y1, x2, y2);
            this.hotspot.put(name, region);
        }
        int ticks = input.readInt();
        for (int i = 0; i < ticks; ++i) {
            this.tick.add(input.readUTF());
        }
        int exits = input.readInt();
        for (int i = 0; i < exits; ++i) {
            this.exit.add(input.readUTF());
        }
        int entries = input.readInt();
        for (int i = 0; i < entries; ++i) {
            this.entry.add(input.readUTF());
        }
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeInt(this.hotspot.size());
        for (Map.Entry<String, Region> entry : this.hotspot.entrySet()) {
            output.writeUTF(entry.getKey());
            output.writeInt(entry.getValue().x1);
            output.writeInt(entry.getValue().y1);
            output.writeInt(entry.getValue().x2);
            output.writeInt(entry.getValue().y2);
        }
        output.writeInt(this.tick.size());
        for (String entry : this.tick) {
            output.writeUTF(entry);
        }
        output.writeInt(this.exit.size());
        for (String entry : this.exit) {
            output.writeUTF(entry);
        }
        output.writeInt(this.entry.size());
        for (String _entry : this.entry) {
            output.writeUTF(_entry);
        }
    }
}

