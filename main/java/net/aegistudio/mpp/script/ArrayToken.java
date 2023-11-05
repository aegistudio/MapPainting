/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.script;

import javax.script.ScriptEngine;

public interface ArrayToken {
    public Object create(ScriptEngine var1) throws Exception;

    public long length(ScriptEngine var1, Object var2) throws Exception;

    public Object index(ScriptEngine var1, Object var2, int var3) throws Exception;

    public void add(ScriptEngine var1, Object var2, Object var3) throws Exception;
}

