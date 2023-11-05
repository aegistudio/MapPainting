/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp.script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.TreeMap;
import javax.script.Invocable;
import org.bukkit.command.CommandSender;

public class Callback {
    public SubCallback nullCallback = new SubCallback();
    public TreeMap<String, SubCallback> groupCallback = new TreeMap();
    public String currentState = null;
    public SubCallback currentCallback = this.nullCallback;
    private Invocable script;

    public void hotspot(String group, String handle, int x1, int y1, int x2, int y2) {
        this.group(group).hotspot(handle, x1, y1, x2, y2);
    }

    public void tick(String group, String handle) {
        this.group(group).tick(handle);
    }

    public void entry(String group, String handle) {
        this.group(group).entry(handle);
    }

    public void exit(String group, String handle) {
        this.group(group).exit(handle);
    }

    public void unregister(String group, String handle) {
        this.group(group).remove(handle);
    }

    public SubCallback group(String group) {
        if (group == null) {
            return this.nullCallback;
        }
        SubCallback groupInstance = this.groupCallback.get(group);
        if (groupInstance == null) {
            groupInstance = new SubCallback();
            this.groupCallback.put(group, groupInstance);
        }
        return groupInstance;
    }

    public String current() {
        return this.currentState;
    }

    public SubCallback group() {
        return this.currentCallback;
    }

    public void setScript(Invocable script) {
        this.script = script;
    }

    public void change(String group) {
        String prestate = this.currentState;
        String poststate = group;
        if (prestate == null ? poststate == null : prestate.equals(poststate)) {
            return;
        }
        if (this.script != null) {
            this.currentCallback.exitTrigger(this.script, prestate, poststate);
            this.currentCallback = this.group(poststate);
            this.currentState = poststate;
            this.currentCallback.entryTrigger(this.script, prestate, poststate);
        }
    }

    void tickTrigger() {
        this.currentCallback.tickTrigger(this.script);
    }

    void tapTrigger(int x, int y, CommandSender who, boolean rightHanded) {
        this.currentCallback.tapTrigger(this.script, x, y, who, rightHanded);
    }

    public void read(DataInputStream input) throws Exception {
        this.nullCallback.read(input);
        this.groupCallback.clear();
        int entryCount = input.readInt();
        for (int i = 0; i < entryCount; ++i) {
            String entry = input.readUTF();
            this.group(entry).read(input);
        }
        this.currentState = null;
        if (input.readBoolean()) {
            this.currentState = input.readUTF();
        }
        this.currentCallback = this.group(this.currentState);
    }

    public void write(DataOutputStream output) throws Exception {
        this.nullCallback.write(output);
        output.writeInt(this.groupCallback.size());
        for (Map.Entry<String, SubCallback> engine : this.groupCallback.entrySet()) {
            output.writeUTF(engine.getKey());
            engine.getValue().write(output);
        }
        output.writeBoolean(this.currentState != null);
        if (this.currentState != null) {
            output.writeUTF(this.currentState);
        }
    }
}

