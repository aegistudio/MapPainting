package net.aegistudio.mpp.script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.script.Invocable;

import org.bukkit.command.CommandSender;

/**
 * Callback is the table of registered functions.
 * When events get captured happen, the canvas holder
 * will look up callback table, call functions that 
 * suits the requirement.
 * 
 * The callback function will be identified by name,
 * so don't try to register twice.
 * 
 * @author aegistudio
 */

public class Callback {
	/**
	 * Global callback holder.
	 */
	public SubCallback nullCallback = new SubCallback();
	public TreeMap<String, SubCallback> groupCallback = new TreeMap<String, SubCallback>();
	
	/**
	 * Global state holder.
	 */
	public String currentState = null;
	public SubCallback currentCallback = nullCallback;
	
	/**
	 * Functions registered to hotspot should match the parameter list of (x, y, sender).
	 * Where x, y is the point that the player touches, while player who touches the canvas.
	 */
	public void hotspot(String group, String handle, int x1, int y1, int x2, int y2) {
		this.group(group).hotspot(handle, x1, y1, x2, y2);
	}

	/**
	 * Functions registered to tick should match the parameter list of ().
	 */
	public void tick(String group, String handle) {
		this.group(group).tick(handle);
	}

	/**
	 * Functions registered to entry should match the parameter list of (out, in).
	 * Where out is the former state, and in is the new state.
	 */
	public void entry(String group, String handle) {
		this.group(group).entry(handle);
	}

	/**
	 * Functions registered to entry should match the parameter list of (out, in).
	 * Where out is the former state, and in is the new state.
	 */
	public void exit(String group, String handle) {
		this.group(group).exit(handle);
	}
	
	/**
	 * Abandon a registered callback.
	 */
	public void unregister(String group, String handle) {
		this.group(group).remove(handle);
	}

	public SubCallback group(String group) {
		if(group == null) return this.nullCallback;
		SubCallback groupInstance = this.groupCallback.get(group);
		if(groupInstance == null) this.groupCallback.put(group, 
				groupInstance = new SubCallback());
		return groupInstance;
	}
	
	/**
	 * Get the current group name of the callback.
	 */
	public String current() {
		return this.currentState;
	}

	/**
	 * Get the current group callback list of the callback.
	 */
	public SubCallback group() {
		return this.currentCallback;
	}
	
	private Invocable script;
	public void setScript(Invocable script) {
		this.script = script;
	}
	
	/**
	 * Switch the group of the callback.
	 * @param group
	 */
	public void change(String group) {
		String prestate = this.currentState;
		String poststate = group;
		
		if(prestate == null) {
			 if(poststate == null) return;
		}
		else if(prestate.equals(poststate)) return;
		
		if(this.script != null) {
			this.currentCallback.exitTrigger(script, prestate, poststate);
			this.currentCallback = this.group(poststate);
			this.currentState = poststate;
			this.currentCallback.entryTrigger(script, prestate, poststate);
		}
	}
	
	void tickTrigger() {
		this.currentCallback.tickTrigger(script);
	}
	
	void tapTrigger(int x, int y, CommandSender who, boolean rightHanded) {
		this.currentCallback.tapTrigger(script, x, y, who, rightHanded);
	}
	
	public void read(DataInputStream input) throws Exception {
		this.nullCallback.read(input);
		
		this.groupCallback.clear();
		int entryCount = input.readInt();
		for(int i = 0; i < entryCount; i ++) {
			String entry = input.readUTF();
			this.group(entry).read(input);
		}
		
		this.currentState = null;
		if(input.readBoolean()) this.currentState = input.readUTF();
		this.currentCallback = this.group(currentState);
	}
	
	public void write(DataOutputStream output) throws Exception {
		this.nullCallback.write(output);
		
		output.writeInt(this.groupCallback.size());
		for(Entry<String, SubCallback> engine : this.groupCallback.entrySet()) {
			output.writeUTF(engine.getKey());
			engine.getValue().write(output);
		}
		
		output.writeBoolean(currentState != null);
		if(currentState != null) output.writeUTF(currentState);
	}
}
