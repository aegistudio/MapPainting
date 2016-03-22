package net.aegistudio.mpp.script;

import java.lang.reflect.Method;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;

public class EcmaArrayToken implements ArrayToken {

	@Override
	public Object create(ScriptEngine engine) throws Exception {
		return engine.eval("new Array();");
	}

	@Override
	public long length(ScriptEngine engine, Object array) throws Exception {
		Method getMember = array.getClass().getMethod("getMember", String.class);
		return (long)(Long)getMember.invoke(array, "length");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object index(ScriptEngine engine, Object array, int index) {
		return ((Map<Integer, Object>)array).get(Integer.toString(index));
	}

	@Override
	public void add(ScriptEngine engine, Object array, Object element) throws Exception {
		((Invocable)engine).invokeMethod(array, "push", element);
	}

}
