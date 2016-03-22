package net.aegistudio.mpp.script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import java.util.TreeMap;
import java.util.TreeSet;

import javax.script.Invocable;

/**
 * A callback table dedicated to a specified state.
 * It would be faster if I would like to call a 
 * bundle of functions.
 * 
 * @author aegistudio
 */

public class SubCallback {
	public TreeMap<String, Region> hotspot = new TreeMap<String, Region>();
	public TreeSet<String> tick = new TreeSet<String>();
	public TreeSet<String> exit = new TreeSet<String>();
	public TreeSet<String> entry = new TreeSet<String>();
	
	public void remove(String target) {
		hotspot.remove(target);
		tick.remove(target);
		exit.remove(target);
		entry.remove(target);
	}
	
	public void hotspot(String handle, int x1, int y1, int x2, int y2) {
		hotspot.put(handle, new Region(x1, y1, x2, y2));
	}
	
	public void tick(String handle) {
		tick.add(handle);
	}
	
	public void exit(String handle) {
		exit.add(handle);
	}
	
	public void entry(String handle) {
		entry.add(handle);
	}
	
	void exitTrigger(Invocable script, String prestate, String poststate) {
		for(String _exit : exit) try {
			script.invokeFunction(_exit, prestate, poststate);
		} catch(Throwable t) { t.printStackTrace(); }
	}
	
	void entryTrigger(Invocable script, String prestate, String poststate) {
		for(String _entry : entry) try {
			script.invokeFunction(_entry, prestate, poststate);
		} catch(Throwable t) { t.printStackTrace(); }
	}
	
	void tapTrigger(Invocable script, int x, int y, CommandSender who) {
		if(script != null) {
			for(Entry<String, Region> tap : hotspot.entrySet()) try {
				if(tap.getValue().inside(x, y)) script.invokeFunction(tap.getKey(), x, y, who);
			} catch(Throwable t) { t.printStackTrace(); }
		}
	}
	
	void tickTrigger(Invocable script) {
		if(script != null) {
			for(String _tick : tick) try {
				script.invokeFunction(_tick);
			} catch(Throwable t) { t.printStackTrace(); }
		}
	}
	
	public void read(DataInputStream input) throws IOException {
		this.hotspot.clear();
		this.tick.clear();
		this.exit.clear();
		this.entry.clear();
		
		int hotspots = input.readInt();
		for(int i = 0; i < hotspots; i ++) {
			String name = input.readUTF();
			int x1 = input.readInt();
			int y1 = input.readInt();
			int x2 = input.readInt();
			int y2 = input.readInt();
			Region region = new Region(x1, y1, x2, y2);
			hotspot.put(name, region);
		}
		
		int ticks = input.readInt();
		for(int i = 0; i < ticks; i ++) tick.add(input.readUTF());
		
		int exits = input.readInt();
		for(int i = 0; i < exits; i ++) exit.add(input.readUTF());
		
		int entries = input.readInt();
		for(int i = 0; i < entries; i ++) entry.add(input.readUTF());
	}
	
	public void write(DataOutputStream output) throws IOException {
		output.writeInt(hotspot.size());
		for(Entry<String, Region> entry : hotspot.entrySet()) {
			output.writeUTF(entry.getKey());
			output.writeInt(entry.getValue().x1);
			output.writeInt(entry.getValue().y1);
			output.writeInt(entry.getValue().x2);
			output.writeInt(entry.getValue().y2);
		}
		
		output.writeInt(tick.size());
		for(String entry : tick) output.writeUTF(entry);
		
		output.writeInt(exit.size());
		for(String entry : exit) output.writeUTF(entry);
		
		output.writeInt(entry.size());
		for(String _entry : entry) output.writeUTF(_entry);
	}
}