package net.aegistudio.mpp.script;

import java.util.TreeMap;
import java.util.TreeSet;

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
}