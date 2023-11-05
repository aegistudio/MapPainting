/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.script;

import java.util.TreeMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ScriptEnginePool {
    public static final ScriptEngineManager manager = new ScriptEngineManager();
    public static final TreeMap<String, ScriptEngineFactory> factories = new TreeMap();

    public static void replace(String extension, String name) {
        ScriptEngine engine = manager.getEngineByName(name);
        if (engine != null) {
            factories.put(extension, engine.getFactory());
        }
    }

    static {
        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            for (String extension : factory.getExtensions()) {
                factories.put(extension, factory);
            }
        }
    }
}

