/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.script;

import java.lang.reflect.Method;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

public class EcmaArrayToken
implements ArrayToken {
    @Override
    public Object create(ScriptEngine engine) throws Exception {
        return engine.eval("new Array();");
    }

    @Override
    public long length(ScriptEngine engine, Object array) throws Exception {
        Method getMember = array.getClass().getMethod("getMember", String.class);
        Object result = getMember.invoke(array, "length");
        if (result instanceof Integer) {
            return ((Integer)result).intValue();
        }
        if (result instanceof Long) {
            return (Long)result;
        }
        if (result instanceof Float) {
            return (long)((Float)result).floatValue();
        }
        throw new Exception("Unrecognized type " + result.getClass());
    }

    @Override
    public Object index(ScriptEngine engine, Object array, int index) {
        return ((Map)array).get(Integer.toString(index));
    }

    @Override
    public void add(ScriptEngine engine, Object array, Object element) throws Exception {
        ((Invocable)((Object)engine)).invokeMethod(array, "push", element);
    }
}

