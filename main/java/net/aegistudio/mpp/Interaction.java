/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.CommandSender
 */
package net.aegistudio.mpp;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;

public class Interaction {
    public final int x;
    public final int y;
    public final CommandSender sender;
    public final Location blockLocation;
    public final Location frameLocation;
    public final boolean rightHanded;
    public final ItemFrame itemFrameEntity;

    public Interaction(int x, int y, CommandSender sender, Location blockLocation, Location frameLocation, boolean rightHanded, ItemFrame itemFrameEntity) {
        this.x = x;
        this.y = y;
        this.blockLocation = blockLocation;
        this.frameLocation = frameLocation;
        this.sender = sender;
        this.rightHanded = rightHanded;
        this.itemFrameEntity = itemFrameEntity;
    }

    public Interaction reCoordinate(int x, int y) {
        return new Interaction(x, y, this.sender, this.blockLocation, this.frameLocation, this.rightHanded, this.itemFrameEntity);
    }
}

