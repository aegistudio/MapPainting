package net.aegistudio.mpp.script;

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
		this.group(group).hotspot.put(handle, new Region(x1, y1, x2, y2));
	}

	/**
	 * Functions registered to tick should match the parameter list of ().
	 */
	public void tick(String group, String handle) {
		this.group(group).tick.add(handle);
	}

	/**
	 * Functions registered to entry should match the parameter list of (out, in).
	 * Where out is the former state, and in is the new state.
	 */
	public void entry(String group, String handle) {
		this.group(group).entry.add(handle);
	}

	/**
	 * Functions registered to entry should match the parameter list of (out, in).
	 * Where out is the former state, and in is the new state.
	 */
	public void exit(String group, String handle) {
		this.group(group).exit.add(handle);
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
			for(String exit : this.currentCallback.exit) try {
				this.script.invokeFunction(exit, prestate, poststate);
			} catch(Throwable t) { t.printStackTrace(); }
			
			for(String entry : this.currentCallback.entry) try {
				this.script.invokeFunction(entry, prestate, poststate);
			} catch(Throwable t) { t.printStackTrace(); }
		}
	}
	
	public void tickTrigger() {
		if(this.script != null) {
			for(String tick : this.currentCallback.tick) try {
				this.script.invokeFunction(tick);
			} catch(Throwable t) { t.printStackTrace(); }
		}
	}
	
	public void tapTrigger(int x, int y, CommandSender who) {
		if(this.script != null) {
			for(Entry<String, Region> tap : this.currentCallback.hotspot.entrySet()) try {
				if(tap.getValue().inside(x, y)) this.script.invokeFunction(tap.getKey(), x, y, who);
			} catch(Throwable t) { t.printStackTrace(); }
		}
	}
}
