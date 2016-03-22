package net.aegistudio.mpp.script;

import java.util.TreeMap;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ScriptEnginePool {
	public static final ScriptEngineManager manager = new ScriptEngineManager();
	public static final TreeMap<String, ScriptEngineFactory> factories = new TreeMap<String, ScriptEngineFactory>();
	static {
		for(ScriptEngineFactory factory : manager.getEngineFactories())
			factories.put(factory.getLanguageName(), factory);
	}
}