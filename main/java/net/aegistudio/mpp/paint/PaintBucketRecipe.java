
package net.aegistudio.mpp.paint;

import java.awt.Color;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;


public class PaintBucketRecipe implements Module {
	
	// String localization
	
    public static final String BUCKET_DRINK_TOKEN 		= "@palette.bucket.drink";
    public static final String BUCKET_DRINK_INVARIANT 	= "GLUG, GLUG, GLUG!"; 
    public String drinkPaintBucket 						= BUCKET_DRINK_INVARIANT;
    
    public static final String BUCKET_FILLED_TOKEN 		= "@palette.bucket.filled";
    public static final String BUCKET_FILLED_INVARIANT 	= "Filled Paint Bucket"; 
    public String fillBucket 							= BUCKET_FILLED_INVARIANT;
    
    // Quick reference to the main plugin for ease of accessing other classes
    public MapPainting plugin;
    
    
    /**
     * Checks if an item is a milk bucket or not.
     * @param item - An ItemStack that should be a milk bucket
     * @return boolean - If the item is a milk bucket or not.
     */
    public boolean isItemMilkBucket(ItemStack item) {
    	return item.getType() == Material.MILK_BUCKET;
    }

    
    /**
     * Gets the color of the milk bucket item supplied. Returns null if not a milk bucket.
     * @param item - An ItemStack that should be a milk bucket
     * @return Color or null - The color of the paint bucket, null if item is incorrectly typed
     */
    public Color getColor(ItemStack item) {
    	if (!isItemMilkBucket(item)) return null;
    	Color resultColor = plugin.m_paintManager.getItemColor(item);
    	return resultColor;
    }

    
    /**
     * Sets a passed milk bucket ItemStack to the paint color supplied. No op if not a milk bucket.
     * @param item - An ItemStack that should be a milk bucket
     * @param color - The new color for the resulting paint bucket
     */
    public void setColor(ItemStack item, Color color) {
    	if (!isItemMilkBucket(item)) return;
        plugin.m_paintManager.setColor(item, color);
    }

    
    /**
     * Adds a paint bucket listener that handles interactions related to paint buckets
     */
    public void RegisterPaintBucketListener() {
    	PaintBucketListener listener = new PaintBucketListener(plugin.m_paintManager);
        plugin.getServer().getPluginManager().registerEvents((Listener)listener, (Plugin)plugin);
    }
    
    
    /**
     * Defines and registers the recipe for paint buckets
     */
    @SuppressWarnings("deprecation")
	public void AddPaintBucketRecipe() {
    	
    	PaintManager PM = plugin.m_paintManager;
    	
    	// Define the resulting item
    	ItemStack result = new ItemStack(Material.MILK_BUCKET);
        
        // Define the unique recipe key
        NamespacedKey recipeName = new NamespacedKey(plugin, "paintbuckets");
        
        // Define the recipe
        ShapelessRecipe recipe = new ShapelessRecipe(recipeName, result);
        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.MILK_BUCKET);
        
        // Register the recipe
        plugin.getServer().addRecipe((Recipe)recipe);
    }
    
    
    /**
     * Mandatory override declaration for modules.
     * Runs when the main plugin is loaded
     * @param plugin - Reference to the main plugin loaded
     * @param config - Reference to the main configuration file settings loaded
     */
    @Override
    public void load(MapPainting plugin, ConfigurationSection config) throws Exception {
        
    	// Update the uninstantiated plugin reference
    	this.plugin = plugin;
    	
        // Localize paint bucket related strings
        this.drinkPaintBucket = plugin.getLocale(BUCKET_DRINK_TOKEN, BUCKET_DRINK_INVARIANT, config);
        this.fillBucket = plugin.getLocale(BUCKET_FILLED_TOKEN, BUCKET_FILLED_INVARIANT, config);
        
        AddPaintBucketRecipe();
        RegisterPaintBucketListener();
    }

    
    /**
     * Mandatory override declaration for modules
     * @param plugin - Reference to the main plugin loaded
     * @param config - Reference to the main configuration file settings loaded
     */
    @Override
    public void save(MapPainting plugin, ConfigurationSection config) throws Exception {
    }
    
    
}

