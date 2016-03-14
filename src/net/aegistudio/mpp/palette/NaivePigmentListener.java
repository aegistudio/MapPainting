package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class NaivePigmentListener implements Listener {
	private final PaletteManager palette;
	public NaivePigmentListener(PaletteManager palette) {
		this.palette = palette;
	}
	
	@EventHandler
	public void onCraftPigment(PrepareItemCraftEvent e) {
		if(e.getRecipe() instanceof ShapelessRecipe) {
			// Check if it is a naive pigment recipe.
			ItemStack result = e.getInventory().getResult();
			if(result.getType() != Material.INK_SACK) return;
			
			int sumCyan = 0, sumMagenta = 0, sumYellow = 0;	
			int inspected = 0; boolean blend = false;
			
			for(ItemStack item : e.getInventory().getMatrix())
				if(item != null) if(item.getType() != Material.AIR)
					if(item.getType() == Material.WATER_BUCKET) blend = true;
					else if(item.getType() != Material.INK_SACK) return;
					else {
						Color color = this.palette.dye.getColor(item);
						sumCyan += (255 - color.getRed());
						sumMagenta += (255 - color.getGreen());
						sumYellow += (255 - color.getBlue());
						inspected += 1;
					}
			int red, green, blue;
			
			if(blend) {
				red = Math.max(255 - sumCyan, 0);
				green = Math.max(255 - sumMagenta, 0);
				blue = Math.max(255 - sumYellow, 0);
			}
			else {
				red = 255 - sumCyan / inspected;
				green = 255 - sumMagenta / inspected;
				blue = 255 - sumYellow / inspected;
			}
			
			this.palette.dye.setColor(result, new Color(red, green, blue));
			e.getInventory().setResult(result);
		}
	}
}
