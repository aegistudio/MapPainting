package net.aegistudio.mpp.palette;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * Naive pigment recipe means to blend several dyes directly
 * in inventory, their color will be blended.
 * @author aegistudio
 */

public class NaivePigmentRecipe extends ShapelessRecipe {

	private final int count;
	public NaivePigmentRecipe(int count) {
		super(new ItemStack(Material.INK_SACK, count));
		this.count = count;
		for(int i = 0; i < this.count; i ++)
			super.addIngredient(Material.INK_SACK);
	}
}
