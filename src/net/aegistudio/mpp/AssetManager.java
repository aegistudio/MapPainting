/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp;

import java.util.TreeMap;
import net.aegistudio.mpp.export.Asset;
import net.aegistudio.mpp.export.AssetService;

public class AssetManager
extends TreeMap<String, Asset>
implements Asset,
AssetService {
    private static final long serialVersionUID = 1L;

    @Override
    public <A extends Asset> A get(String name, Class<A> clazz) {
        if (name == null) {
            return null;
        }
        int dot = name.indexOf(46);
        if (dot < 0) {
            return (A)((Asset)super.get(name));
        }
        return ((AssetManager)super.get(name.substring(0, dot))).get(name.substring(dot + 1), clazz);
    }

    @Override
    public Asset put(String name, Asset asset) {
        if (name == null) {
            return null;
        }
        int dot = name.indexOf(46);
        if (dot < 0) {
            return super.put(name, asset);
        }
        return ((AssetManager)super.get(name.substring(0, dot))).put(name.substring(dot + 1), asset);
    }

    @Override
    public void group(String name) {
        this.put(name, new AssetManager());
    }
}

