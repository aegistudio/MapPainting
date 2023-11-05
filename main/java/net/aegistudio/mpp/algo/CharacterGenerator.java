/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.export.Asset;

public interface CharacterGenerator
extends Asset {
    public void chargen(Paintable var1, int var2, int var3, int var4, int var5, char var6);

    public int chargen(Paintable var1, int var2, int var3, float var4, char var5);

    public int metricWidth(float var1, char var2);

    public int metricHeight(float var1, char var2);
}

