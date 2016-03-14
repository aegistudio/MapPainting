package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class NaivePigmentRecipe implements Module {
	@SuppressWarnings("deprecation")
	public Color getColor(ItemStack dye) {
		java.awt.Color awtColor = painting.palette.getColor(dye);
		if(awtColor != null) return awtColor;
		org.bukkit.Color color = DyeColor.getByDyeData((byte) dye.getDurability()).getColor();
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	@SuppressWarnings("deprecation")
	public void setColor(ItemStack dye, Color newColor) {
		painting.palette.setColor(dye, newColor);
		int distance = Integer.MAX_VALUE;
		short damage = 0;
		for(DyeColor color : DyeColor.values()) {
			int redDistance = color.getColor().getRed() - newColor.getRed();
			int greenDistance = color.getColor().getGreen() - newColor.getGreen();
			int blueDistance = color.getColor().getBlue() - newColor.getBlue();
			int newDistance = redDistance * redDistance + greenDistance * greenDistance 
					+ blueDistance * blueDistance;
			if(newDistance < distance) {
				distance = newDistance;
				damage = color.getDyeData();
			}
		}
		dye.setDurability(damage);
	}

	public MapPainting painting;
	@SuppressWarnings("deprecation")
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		
		for(int i = 2; i <= 9; i ++) {
			ShapelessRecipe shapeless = new ShapelessRecipe(new ItemStack(Material.INK_SACK, i));
			shapeless.addIngredient(i, Material.INK_SACK, -1);
			painting.getServer().addRecipe(shapeless);
			
			if(i >= 3) {
				ShapelessRecipe blend = new ShapelessRecipe(new ItemStack(Material.INK_SACK));
				blend.addIngredient(i - 1, Material.INK_SACK, -1);
				blend.addIngredient(Material.WATER_BUCKET);
				painting.getServer().addRecipe(blend);
			}
		}
		
		painting.getServer().getPluginManager()
			.registerEvents(new NaivePigmentListener(painting.palette), painting);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}
}
