package net.aegistudio.mpp.script;

import java.io.InputStream;
import java.util.TreeMap;

import javax.script.ScriptEngine;

/**
 * The cassette provides an easy way to access by runtime
 * objects. It is possible to store state, object, etc into
 * the casette!
 * 
 * @author aegistudio
 */

public class Cassette extends TreeMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public void read(InputStream input, ScriptEngine engine) {
		
	}
}
