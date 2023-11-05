/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.export;

public class NamingException
extends Exception {
    private static final long serialVersionUID = 1L;
    private final String naming;
    private final Object value;

    public NamingException(String name, Object value) {
        super(name + ": " + value);
        this.naming = name;
        this.value = value;
    }

    public String getNaming() {
        return this.naming;
    }

    public Object getValue() {
        return this.value;
    }

    public <T> T getValue(Class<T> t) {
        return (T)this.value;
    }
}

