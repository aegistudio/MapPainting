package net.aegistudio.mpp.script;

import javax.script.ScriptEngine;

public interface ArrayToken {
	public Object create(ScriptEngine engine) throws Exception;
	
	public long length(ScriptEngine engine, Object array) throws Exception;
	
	public Object index(ScriptEngine engine, Object array, int index) throws Exception;
	
	public void add(ScriptEngine engine, Object array, Object element) throws Exception;
}
