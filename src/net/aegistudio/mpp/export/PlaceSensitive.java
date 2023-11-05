/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Item
 */
package net.aegistudio.mpp.export;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

public interface PlaceSensitive {
    public void place(Location var1, BlockFace var2);

    public void unplace(Item var1);
}

