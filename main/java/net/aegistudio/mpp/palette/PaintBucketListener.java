package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffectType;

public class PaintBucketListener implements Listener {
	private final PaletteManager palette;
	public PaintBucketListener(PaletteManager palette) {
		this.palette = palette;
	}
	
	@EventHandler
	public void onCraftPaintBucket(PrepareItemCraftEvent e) {
		if(e.getRecipe() instanceof ShapelessRecipe) {
			ItemStack result = e.getInventory().getResult();
			if(result.getType() != Material.MILK_BUCKET) return;
			
			int sumCyan = 0, sumMagenta = 0, sumYellow = 0;	
			
			for(ItemStack item : e.getInventory().getMatrix())
				if(item != null) if(item.getType() != Material.AIR) {
					Color color;
					if(item.getType() == Material.MILK_BUCKET) {
						color = palette.paintBucket.getColor(item);
						if(color == null) continue;
					}
					else if(item.getType() != Material.INK_SACK) return;
					else color = this.palette.dye.getColor(item);
					
					sumCyan += (255 - color.getRed());
					sumMagenta += (255 - color.getGreen());
					sumYellow += (255 - color.getBlue());
				}
			
			int red, green, blue;
			red = Math.max(255 - sumCyan, 0);
			green = Math.max(255 - sumMagenta, 0);
			blue = Math.max(255 - sumYellow, 0);
			
			this.palette.setColor(result, new Color(red, green, blue));
			e.getInventory().setResult(result);
		}
	}
	
	@EventHandler
	public void postCraft(CraftItemEvent e) {
		if(e.isCancelled()) return;
		
		if(e.getRecipe() instanceof ShapelessRecipe) {
			ItemStack result = e.getInventory().getResult();
			if(result.getType() != Material.MILK_BUCKET) return;
			
			ItemStack[] matrix = e.getInventory().getMatrix();
			for(int i = 0; i < matrix.length; i ++) {
				ItemStack item = matrix[i];
				if(item != null) if(item.getType() != Material.AIR) {
					if(item.getType() != Material.MILK_BUCKET) 
						if(item.getType() != Material.INK_SACK) return;
					matrix[i] = null;
				}
			}
			e.getInventory().setMatrix(matrix);
			e.getInventory().setResult(result);
		}
	}
	
	PotionEffectType[] rewards = new PotionEffectType[]{
			PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION,
			PotionEffectType.HARM, PotionEffectType.HUNGER,
			PotionEffectType.POISON, PotionEffectType.WEAKNESS};
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void isPaintBucketTasty(PlayerItemConsumeEvent e) {
		if(e.getItem().getType() != Material.MILK_BUCKET) return;
		if(palette.paintBucket.getColor(e.getItem()) == null) return;
		
		rewards[(int) (rewards.length * Math.random())]
				.createEffect(2000, 1).apply(e.getPlayer());
		e.getPlayer().sendMessage(palette.paintBucket.drinkPaintBucket);
		
		ItemStack item = new ItemStack(Material.BUCKET, 1);
		e.getPlayer().setItemInHand(item);
		
		e.setCancelled(true);
	}
}
