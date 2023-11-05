/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp.tool;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Framer
extends Pencil {
    public Framer() {
        this.tapMessage = "@memento.framerTap";
        this.lineMessage = "@memento.framerLine";
    }

    @Override
    public boolean paint(ItemStack heldItem, MapCanvasRegistry canvas, Interaction interact) {
        
    	// ensure painter is a player to prevent errors
    	if (!(interact.sender != null && interact.sender instanceof Player)) {
    		return false;
    	}
    	
    	// ensure only right clicking frames the painting
    	if (!interact.rightHanded) {
    		return false;
    	}
    	
    	Player player = ((Player)interact.sender);
    	
    	if (heldItem.getType() == Material.ITEM_FRAME) {
            ItemFrame frame = interact.itemFrameEntity;
            
            if (frame.isVisible()){
            	frame.setVisible(false);
            	
            } else {
            	frame.setVisible(true);
            	
            	// remove the item frame from the player (is only ever functional in main hand)
                if (player.getGameMode() == GameMode.SURVIVAL) {
                	int newAmount = player.getInventory().getItemInMainHand().getAmount() - 1;
                    if (newAmount > 0) {
                    	player.getInventory().getItemInMainHand().setAmount(newAmount);
                    } else {
                    	player.getInventory().setItemInMainHand(null);
                    }
                }
            	
            }            
            
            return true;
        }
    	
        return false;
    }
}

