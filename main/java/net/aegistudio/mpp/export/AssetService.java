/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.export;

public interface AssetService {
    public <A extends Asset> A get(String var1, Class<A> var2);

    public <A extends Asset> Asset put(String var1, A var2);

    public void group(String var1);
}

