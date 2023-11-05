
package net.aegistudio.mpp.paint;

import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffectType;

/**
 * Defines event listeners related to paint bucket recipe crafting (...and consumption)
 * Canvas interactions are not managed here.
 */
public class PaintBucketListener implements Listener {
    
	private PaintManager PM;
    PotionEffectType[] rewards = new PotionEffectType[]{PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.WEAKNESS};

    
    /**
     * Constructor
     * Defines event listeners related to paint bucket recipe crafting (...and consumption)
     * Canvas interactions are not managed here.
     */
    public PaintBucketListener(PaintManager paintManager) {
        PM = paintManager;
    }

    
    /**
     * Handles updating the crafting result to be a proper paint bucket with a color matching the input potion.
     * If the milk bucket also has paint lore it will mix the two colors evenly
     */
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCraftPaintBucket(PrepareItemCraftEvent e) {
    		
        if (e.getRecipe() instanceof ShapelessRecipe) {
        	
        	// get the standard recipe crafting result
            ItemStack result = e.getInventory().getResult();
            
            // Abort unless it is a milk bucket
            if (result.getType() != Material.MILK_BUCKET) {
                return;
            }
            
            // We start out white because it is milk based
            // This will be the color result if we mix milk bucket and a water bottle with no additional color lore
            Color bucketColor = new Color(255, 255, 255);
            Color potionColor = new Color(255, 255, 255);
            boolean mix = false;
            
            // Check the whole crafting grid for relevant items
            for (ItemStack item : e.getInventory().getMatrix()) {
            	
            	// Guard against empty crafting grid slots otherwise item.getType() fails
            	if (item == null) {
            		continue;
            	}
            	
            	if ((item.getType() == Material.POTION) && (PM.itemHasPaintLore(item))) {
	                potionColor = PM.getItemColor(item);
            	}
            	
            	if ((item.getType() == Material.MILK_BUCKET) && (PM.itemHasPaintLore(item))) {
	                bucketColor = PM.getItemColor(item);
	                mix = true;
            	}
            }
            
            // Calculate the final resulting color of the craft
            Color finalColor = potionColor;
            if (mix) {
	            int red = (bucketColor.getRed() + potionColor.getRed()) / 2;
	            int green = (bucketColor.getGreen() + potionColor.getGreen()) / 2;
	            int blue = (bucketColor.getBlue() + potionColor.getBlue()) / 2;
	            finalColor = new Color(red, green, blue);
            }
            
            // Update the crafting result
            result = this.PM.getPaintBucket(finalColor);
            e.getInventory().setResult(result);
            
        }
    }

    
    /**
     * Handles cleanup of the crafting grid after paint bucket recipe craft is complete.
     */
    @EventHandler
    public void postCraft(CraftItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getRecipe() instanceof ShapelessRecipe) {
            ItemStack result = e.getInventory().getResult();
            if (result.getType() != Material.MILK_BUCKET) {
                return;
            }
            ItemStack[] matrix = e.getInventory().getMatrix();
            for (int i = 0; i < matrix.length; ++i) {
                ItemStack item = matrix[i];
                if (item == null || item.getType() == Material.AIR) continue;
                if (item.getType() != Material.MILK_BUCKET && item.getType() != Material.POTION) {
                    return;
                }
                matrix[i] = null;
            }
            e.getInventory().setMatrix(matrix);
            e.getInventory().setResult(result);
        }
    }

    
    /**
     * Handles consumption of paint buckets.
     */
    @EventHandler
    public void isPaintBucketTasty(PlayerItemConsumeEvent e) {
    	
    	ItemStack eventItem = e.getItem();
    	
    	// Guard against matches to non milk buckets
        if (eventItem.getType() != Material.MILK_BUCKET) {
            return;
        }
        
        // Guard against matches to normal milk buckets 
        if (!PM.itemHasPaintLore(eventItem)) {
            return;
        }
        
        this.rewards[(int)((double)this.rewards.length * Math.random())].createEffect(2000, 1).apply((LivingEntity)e.getPlayer());
        e.getPlayer().sendMessage(this.PM.paintBucketRecipe.drinkPaintBucket);
        ItemStack item = new ItemStack(Material.BUCKET, 1);
        e.getPlayer().setItemInHand(item);
        e.setCancelled(true);
    }
}

