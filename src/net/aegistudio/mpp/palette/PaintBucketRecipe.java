package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import net.md_5.bungee.api.ChatColor;

public class PaintBucketRecipe implements Module {
	public Color getColor(ItemStack fillBucket) {
		if(fillBucket.getType() != Material.MILK_BUCKET) return null;
		return painting.palette.getColor(fillBucket);
	}
	
	public void setColor(ItemStack fillBucket, Color color) {
		if(fillBucket.getType() != Material.MILK_BUCKET) return;
		this.painting.palette.setColor(fillBucket, color);
	}
	
	public static final String FILL_BUCKET = "fillBucket";
	public String fillBucket = ChatColor.RESET + "Fill Bucket";
	
	public static final String DRINK_PAINT_BUCKET = "drinkPaintBucket";
	public String drinkPaintBucket = ChatColor.MAGIC + "====" + 
			ChatColor.RESET + "Is it tasty?" + ChatColor.MAGIC + "=====";
	
	public MapPainting painting;
	@SuppressWarnings("deprecation")
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.painting = painting;
		fillBucket = painting.getLocale(FILL_BUCKET, fillBucket, section);
		drinkPaintBucket = painting.getLocale(DRINK_PAINT_BUCKET, drinkPaintBucket, section);
		
		ItemStack paintBucket = new ItemStack(Material.MILK_BUCKET, 1);
		ItemMeta meta = paintBucket.getItemMeta();
		meta.setDisplayName(fillBucket);
		meta.addEnchant(Enchantment.DURABILITY, 0, false);
		paintBucket.setItemMeta(meta);
		
		ShapelessRecipe shapeless = new ShapelessRecipe(paintBucket);
		shapeless.addIngredient(1, Material.INK_SACK, -1);
		shapeless.addIngredient(Material.MILK_BUCKET);
		painting.getServer().addRecipe(shapeless);
		
		PaintBucketListener listener = new PaintBucketListener(painting.palette);
		painting.getServer().getPluginManager().registerEvents(listener, painting);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}
}
