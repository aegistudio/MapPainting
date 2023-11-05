package net.aegistudio.mpp.paint;

import java.awt.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class PaintBottleListener implements Listener {
    private final PaintManager palette;

    public PaintBottleListener(PaintManager palette) {
        this.palette = palette;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCraftPaintBottle(PrepareItemCraftEvent e) {
    	
        if (e.getRecipe() instanceof ShapelessRecipe) {
            // Check if it is a paint bottle recipe.
            ItemStack result = e.getInventory().getResult();
            if (result.getType() != Material.POTION) return;

            int sumCyan = 0, sumMagenta = 0, sumYellow = 0;
            int inspected = 0;
            boolean blend = false;

            for (ItemStack item : e.getInventory().getMatrix())
                if (item != null) if (item.getType() != Material.AIR)
                    if (item.getType() == Material.WATER_BUCKET) blend = true;
                    else if (item.getType() != Material.POTION) return;
                    else {
                        Color color = this.palette.getItemColor(item);
                        
                        // we need to handle null case because potions without paint lore 
                        // cause exceptions otherwise.
                        if (color != null){
	                        sumCyan += color.getRed();
	                        sumMagenta += color.getGreen();
	                        sumYellow += color.getBlue();
                        }
                        inspected += 1;
                    }
            int red, green, blue;

            if (blend) {
                red = sumCyan > 255 ? 255 : sumCyan;
                green = sumMagenta > 255 ? 255 : sumMagenta;
                blue = sumYellow > 255 ? 255 : sumYellow;
            } else {
                red = (sumCyan / inspected) > 255 ? 255 : (sumCyan / inspected);
                green = (sumMagenta / inspected) > 255 ? 255 : (sumMagenta / inspected);
                blue = (sumYellow / inspected) > 255 ? 255 : (sumYellow / inspected);
            }
            
            result = this.palette.getPaintBottle(new Color(red, green, blue));

            e.getInventory().setResult(result);
        }
    }
}