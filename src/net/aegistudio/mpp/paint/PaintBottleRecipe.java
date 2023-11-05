
package net.aegistudio.mpp.paint;

import java.awt.Color;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;

public class PaintBottleRecipe implements Module {
    public MapPainting painting;

    public Color getColor(ItemStack dye) {
        java.awt.Color awtColor = painting.m_paintManager.getItemColor(dye);
        if(awtColor != null) return awtColor;
        org.bukkit.Color color = ((PotionMeta) dye.getItemMeta()).getColor();
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    public void setColor(ItemStack dye, Color newColor) {
        painting.m_paintManager.setColor(dye, newColor);
        int distance = Integer.MAX_VALUE;
        for (DyeColor color : DyeColor.values()) {
            int greenDistance;
            int blueDistance;
            int redDistance = color.getColor().getRed() - newColor.getRed();
            int newDistance = redDistance * redDistance + (greenDistance = color.getColor().getGreen() - newColor.getGreen()) * greenDistance + (blueDistance = color.getColor().getBlue() - newColor.getBlue()) * blueDistance;
            if (newDistance >= distance) continue;
            distance = newDistance;
        }
        dye.setDurability((short)0);
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        this.painting = painting;
        for (int i = 2; i <= 9; ++i) {
        	
        	NamespacedKey recipe_pigment = new NamespacedKey(painting, "pigment" + i);
            ShapelessRecipe shapeless = new ShapelessRecipe(recipe_pigment, new ItemStack(Material.POTION, i));
            shapeless.addIngredient(i, Material.POTION, -1);
            painting.getServer().addRecipe(shapeless);
            
            if (i < 3) continue;
            
            NamespacedKey recipe_bucket = new NamespacedKey(painting, "pigment_bucket" + i);
            ShapelessRecipe blend = new ShapelessRecipe(recipe_bucket, new ItemStack(Material.POTION));
            blend.addIngredient(i - 1, Material.POTION, -1);
            blend.addIngredient(Material.WATER_BUCKET);
            painting.getServer().addRecipe((Recipe)blend);
        }
        painting.getServer().getPluginManager().registerEvents((Listener)new PaintBottleListener(painting.m_paintManager), (Plugin)painting);
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }
}

