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

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Namer
extends Pencil {
    public Namer() {
        this.tapMessage = "@memento.namerTap";
        this.lineMessage = "@memento.namerLine";
    }

    @Override
    public boolean paint(ItemStack heldItem, MapCanvasRegistry canvas, Interaction interact) {
    	
    	// ensure painter is a player to prevent errors
    	if (!(interact.sender != null && interact.sender instanceof Player)) {
    		return false;
    	}
    	
    	// ensure this only runs with Name Tags
    	if (heldItem.getType() != Material.NAME_TAG) {
    		return false;
    	}
    	
    	// ensure only right clicking names the painting
    	if (!interact.rightHanded) {
    		return false;
    	}
    	
    	// establish references
    	Player player = ((Player)interact.sender);
    	ItemFrame frame = interact.itemFrameEntity;
    	ItemStack framedItem = frame.getItem();
    	Material stackType = framedItem.getType();
    	ItemMeta meta = framedItem.getItemMeta();
    	
    	// ensure this only runs with Name Tags
    	if (stackType != Material.FILLED_MAP) {
    		return false;
    	}
    
    	// name tags name
        String rawString = heldItem.getItemMeta().getDisplayName();
        
        // parse color codes to mc format
        String newName = ChatColor.translateAlternateColorCodes('&', rawString);
  	
        meta.setDisplayName(newName);
        framedItem.setItemMeta(meta);
           	
        // update the actual frame with the changed item
        frame.setItem(framedItem);
        
        // remove the name tag from the player (is only ever functional in main hand)
        if (player.getGameMode() == GameMode.SURVIVAL) {
        	int newAmount = player.getInventory().getItemInMainHand().getAmount() - 1;
            if (newAmount > 0) {
            	player.getInventory().getItemInMainHand().setAmount(newAmount);
            } else {
            	player.getInventory().setItemInMainHand(null);
            }
        }
        
        return true;           
         
    }
    
}

