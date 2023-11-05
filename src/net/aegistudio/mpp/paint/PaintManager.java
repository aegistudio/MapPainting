package net.aegistudio.mpp.paint;

import java.awt.Color;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;


/**
 * Responsible for all paint related items (ie objects that are paint, that players use to paint with).
 * Manages adding their recipes to the server and yields reliable on demand creation of consistent paint objects.
 */
public class PaintManager implements Module {
    
    // Localized strings for the lore color channels
    
    public static final String RED_TOKEN 				= "@lore.color.red";
    public static final String RED_INVARIANT 			= "Red"; 
    public String red		 							= RED_INVARIANT;
    
    public static final String GREEN_TOKEN 				= "@lore.color.green";
    public static final String GREEN_INVARIANT 			= "Green"; 
    public String green		 							= GREEN_INVARIANT;
    
    public static final String BLUE_TOKEN 				= "@lore.color.blue";
    public static final String BLUE_INVARIANT 			= "Blue"; 
    public String blue		 							= BLUE_INVARIANT;
    
    // Localized strings for item naming usage
    
    public static final String BUCKET_NAME_TOKEN 		= "@lore.name.bucket";
    public static final String BUCKET_NAME_INVARIANT 	= "Paint Bucket"; 
    public String bucketName		 					= BUCKET_NAME_INVARIANT;
    
    public static final String BOTTLE_NAME_TOKEN 		= "@lore.name.bottle";
    public static final String BOTTLE_NAME_INVARIANT 	= "Paint Bottle"; 
    public String bottleName		 					= BOTTLE_NAME_INVARIANT;
    
    // Add hooks to allow for later referral to the loaded shapeless recipes
    public PaintBottleRecipe paintBottleRecipe;
    public PaintBucketRecipe paintBucketRecipe;
    
    // Add uninitialized reference to main plugin
    public MapPainting plugin;
    public ConfigurationSection config;
    
    // Lore formatting codes used in a couple of places for convenience
	public String redFormat 	= "\u00A74";
	public String greenFormat 	= "\u00A7a";
	public String blueFormat 	= "\u00A79";
	public String goldFormat 	= "\u00A76";
	public String whiteFormat 	= "\u00A7f";
	public String randomFormat 	= "\u00A7k";
	
	// Define names of colors
	HashMap <Color, String> colorNameMap;
    
    
    /**
     * Checks if an ItemStack has valid paint lore defined.
     * @param item - the ItemStack to check for lore
     * @return true if an item has valid paint lore, false otherwise
     */
    public boolean itemHasPaintLore(ItemStack item) {
    	
    	// Guard against attempts to check null items to prevent exceptions
    	if (item == null) {
    		return false;
    	}
    	
    	// Guard against unexpected item types having paint lore. We only expect these types to have paint lore currently.
    	if (!(item.getType() == Material.POTION || item.getType() == Material.MILK_BUCKET)) {
    		return false;
    	}
    	
    	// Guard against no lore
    	if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return false;
        }
    	
    	// Guard against bad lore state
    	if (getItemColor(item) == null) {
    		return false;
    	}
    	
    	return true;
    }

    
    /**
     * Parses a lore string for a paint color value and returns a ranged cast back to an int.
     * @param plugin - Reference to the main plugin loaded
     * @param config - Reference to the main configuration file settings loaded
     * @return Color or null if item invalid or missing lore
     */
    public int getChannelFromLoreString(String loreString) {
    	int result = Integer.parseInt(ChatColor.stripColor(loreString).split(": ")[1]);
    	if (result < 0) result = 0;
    	if (result > 255) result = 255;
    	return result;
    }
    
    
    /**
     * Gets the color stored in RGB Lore tags for the supplied item.
     * This method assumes that the color stored on an item has individual lines for RGB values
     * RGB values are localized based on tokens for regional support.
     * @param item - The item to attempt to read color lore from.
     * @return Color or null if item invalid or missing lore
     */
    public Color getItemColor(ItemStack item) {
        
    	// Guard against null item
    	if (item == null) {
    		return null;
    	}
    	
    	// Guard against no lore
        List<String> loreLines = item.getItemMeta().getLore();
        if (loreLines == null) {
        	return null;
        }
        
        // Provide invalid defaults so we can verify if valid paint lore was declared
        int dummyValue = -1;
        int redChannel = dummyValue;
        int greenChannel = dummyValue;
        int blueChannel = dummyValue;
        
        // Read the channel strengths from the item(s)
        for (String line : loreLines) {
            
        	if (line.contains(red + ": ")) {
                redChannel = getChannelFromLoreString(line);
            }
            
            else if (line.contains(green + ": ")) {
                greenChannel = getChannelFromLoreString(line);
            } 
            
            else if (line.contains(blue + ": ")) {
            	blueChannel = getChannelFromLoreString(line);
            }
        	
        }
        
        // Guard against partially declared or missing color matches
        if ((redChannel == dummyValue) || (greenChannel == dummyValue) || (blueChannel == dummyValue)) {
        	return null;
        }
        
        return new Color(redChannel, greenChannel, blueChannel);
    }

    
    /**
     * Returns a "Paint Bottle" item which is a potion with paint lore tags matching the supplied Color.
     * @param Color - The color to use when generating the item.
     * @return ItemStack - The colored paint bottle item.
     */
    public ItemStack getPaintBottle(Color color) {
        ItemStack item = new ItemStack(Material.POTION);
        plugin.m_paintManager.setColor(item, color);
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setColor(org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
        meta.setDisplayName(whiteFormat + bottleName + " " + getColorName(color));
        item.setItemMeta(meta);
        return item;
    }

    
    /**
     * Returns a "Paint Bucket" item which is a milk bucket with paint lore tags matching the supplied Color.
     * @param Color - The color to use when generating the item.
     * @return ItemStack - The colored milk bucket item.
     */
    public ItemStack getPaintBucket(Color color) {
        ItemStack item = new ItemStack(Material.MILK_BUCKET);
        plugin.m_paintManager.setColor(item, color);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(whiteFormat + bucketName + " " + getColorName(color));
        item.setItemMeta(meta);
        return item;
    }
    
    
    /**
     * Overwrites the supplied items lore tags with paint lore tags based on the supplied Color.
     * @param Color - The color to use to generate the lore tags.
     * @return ItemStack - The item with updated paint lore tags.
     */
    public void setColor(ItemStack item, Color color) {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> newLore = new ArrayList<String>();
        newLore.add(redFormat 	+ red 	+ ": " + whiteFormat + color.getRed());
        newLore.add(greenFormat + green + ": " + whiteFormat + color.getGreen());
        newLore.add(blueFormat 	+ blue 	+ ": " + whiteFormat + color.getBlue());
        meta.setLore(newLore);
        item.setItemMeta(meta);
    }
    
    
    /**
     * Initializes the paint bottle recipes
     */
    public void InitializePaintBottleRecipe() throws Exception {
    	paintBottleRecipe = new PaintBottleRecipe();
        paintBottleRecipe.load(plugin, config);
    }
    
    
    /**
     * Initializes the paint bucket recipes
     */
    public void InitializePaintBucketRecipe() throws Exception {
    	paintBucketRecipe = new PaintBucketRecipe();
		paintBucketRecipe.load(plugin, config);
    }
    
    
    /**
     * Updates the strings declared in this module to their localized equivalent
     * @param plugin - Reference to the main plugin loaded
     * @param config - Reference to the main configuration file settings loaded
     */
    public void LocalizeStrings() {
        
    	// Channel names
    	red	 		= plugin.getLocale(RED_TOKEN, RED_INVARIANT, config);
        green 		= plugin.getLocale(GREEN_TOKEN, GREEN_INVARIANT, config);
        blue 		= plugin.getLocale(BLUE_TOKEN, BLUE_INVARIANT, config);
        
        // Paint object names
        bottleName 	= plugin.getLocale(BOTTLE_NAME_TOKEN, BOTTLE_NAME_INVARIANT, config);
        bucketName 	= plugin.getLocale(BUCKET_NAME_TOKEN, BUCKET_NAME_INVARIANT, config);
        
        // Color names
        for (Map.Entry<Color ,String> entry : colorNameMap.entrySet()) {
            Color color = entry.getKey();
        	String token = "@lore.rgb." + color.getRed() + "." + color.getGreen() + "." + color.getBlue();
        	String localizedName = plugin.getLocale(token, entry.getValue(), config);
        	entry.setValue(localizedName);
        }
    }

    
    /**
     * Mandatory override declaration for modules.
     * Runs when the main plugin is loaded
     * @param plugin - Reference to the main plugin loaded
     * @param config - Reference to the main configuration file settings loaded
     */
    @Override
    public void load(MapPainting plugin, ConfigurationSection config) throws Exception {
        
    	this.plugin = plugin;
    	this.config = config;
    	
    	DefineColorNames();
    	LocalizeStrings();
        InitializePaintBottleRecipe();
        InitializePaintBucketRecipe();
    }


    /**
     * Mandatory override declaration for modules.
     * @param plugin - Reference to the main plugin
     * @param config - Reference to the main configuration file settings
     */
    @Override
    public void save(MapPainting plugin, ConfigurationSection config) {
    }
    
    
    /**
     * Returns a name for known true colors, or their RGB values otherwise.
     * Intended for use when displaying paint colors.
     * @param Color - The color to get a human display name for.
     * @return String - The human display name for the supplied Color
     */
    public String getColorName(Color color) {

    	String colorName = colorNameMap.get(color); 
    	
    	// default if no name is defined
    	if (colorName == null) {
    		String fallBack = 
    			whiteFormat + "(" + 
    			redFormat + color.getRed() + " " + 
    			greenFormat + color.getGreen() + " " +
    			blueFormat + color.getBlue() + 
    			whiteFormat + ")";
    		return fallBack;
    	}
    	
    	return whiteFormat + "(" + goldFormat + colorName + whiteFormat + ")";
    }
    
    
    /**
     * Defines the names of colors of paint.
     */
    public void DefineColorNames() throws Exception {
    	
    	// actually create the new map
    	colorNameMap = new HashMap <Color, String> ();
    	
    	// The below color values chosen are the only "True" colors that Minecraft maps display 1:1 for.
    	// The list was generated for v1.20.2 by iterating the byte index for
    	// m_canvasManager.color.getIndex which exercises the server MapPalette implementation
    	
    	// The logic behind names chosen are almost entirely arbitrary, 
    	// but were chosen based on what feeling the color evoked.
    	
    	// Values ideally should be unique but this is not enforced (because localization complicates it).
    	
    	// Populate the color to name mappings
    	colorNameMap.put(new Color(0,0,0), "Absolute Black");
        colorNameMap.put(new Color(89,125,39), "Adventurer Green");
        colorNameMap.put(new Color(109,153,48), "Abandon Green");
        colorNameMap.put(new Color(127,178,56), "Toxicity Green");
        colorNameMap.put(new Color(67,94,29), "Snake Dark Green");
        colorNameMap.put(new Color(174,164,115), "Lichen Brown");
        colorNameMap.put(new Color(213,201,140), "Sandstone Brown");
        colorNameMap.put(new Color(247,233,163), "Light Yellow");
        colorNameMap.put(new Color(130,123,86), "Brown Gold");
        colorNameMap.put(new Color(140,140,140), "Solid Grey");
        colorNameMap.put(new Color(171,171,171), "Chalice Silver");
        colorNameMap.put(new Color(199,199,199), "Puritan Silver");
        colorNameMap.put(new Color(105,105,105), "Dreary Dark Grey");
        colorNameMap.put(new Color(180,0,0), "Viscera Red");
        colorNameMap.put(new Color(220,0,0), "Passion Red");
        colorNameMap.put(new Color(255,0,0), "Superior Red");
        colorNameMap.put(new Color(135,0,0), "Pandemonium Dark Red");
        colorNameMap.put(new Color(112,112,180), "Iris Purple");
        colorNameMap.put(new Color(138,138,220), "Periwinkle Purple");
        colorNameMap.put(new Color(160,160,255), "Delirium Dark Purple");
        colorNameMap.put(new Color(84,84,135), "Lavender Dark Purple");
        colorNameMap.put(new Color(117,117,117), "Consistency Grey");
        colorNameMap.put(new Color(144,144,144), "Precision Grey");
        colorNameMap.put(new Color(167,167,167), "Labyrinth Grey");
        colorNameMap.put(new Color(88,88,88), "Challenge Dark Grey");
        colorNameMap.put(new Color(0,87,0), "Lily Green");
        colorNameMap.put(new Color(0,106,0), "Meadow Green");
        colorNameMap.put(new Color(0,124,0), "Vivacious Green");
        colorNameMap.put(new Color(0,65,0), "Backwoods Dark Green");
        colorNameMap.put(new Color(180,180,180), "Chalk White");
        colorNameMap.put(new Color(220,220,220), "Bone White");
        colorNameMap.put(new Color(255,255,255), "Clarity White");
        colorNameMap.put(new Color(135,135,135), "Cornerstone Grey");
        colorNameMap.put(new Color(115,118,129), "Weight Grey");
        colorNameMap.put(new Color(141,144,158), "Reinforcement Grey");
        colorNameMap.put(new Color(164,168,184), "Sobriety Grey");
        colorNameMap.put(new Color(86,88,97), "Millstone Dark Grey");
        colorNameMap.put(new Color(106,76,54), "Bread Brown");
        colorNameMap.put(new Color(130,94,66), "Timber Brown");
        colorNameMap.put(new Color(151,109,77), "Hobbit Brown");
        colorNameMap.put(new Color(79,57,40), "Practicality Dark Brown");
        colorNameMap.put(new Color(79,79,79), "Dove Dark Grey");
        colorNameMap.put(new Color(96,96,96), "Apathy Grey");
        colorNameMap.put(new Color(112,112,112), "Troubled Grey");
        colorNameMap.put(new Color(59,59,59), "Repetition Dark Grey");
        colorNameMap.put(new Color(45,45,180), "Persian Blue");
        colorNameMap.put(new Color(55,55,220), "Palace Blue");
        colorNameMap.put(new Color(64,64,255), "Jubilant Blue");
        colorNameMap.put(new Color(33,33,135), "Contemplation Dark Blue");
        colorNameMap.put(new Color(100,84,50), "Steady Brown");
        colorNameMap.put(new Color(123,102,62), "Lumber Brown");
        colorNameMap.put(new Color(143,119,72), "Salubrious Brown");
        colorNameMap.put(new Color(75,63,38), "Sophisticated Dark Brown");
        colorNameMap.put(new Color(180,177,172), "Foil Silver");
        colorNameMap.put(new Color(220,217,211), "Wisdom White");
        colorNameMap.put(new Color(255,252,245), "Silence White");
        colorNameMap.put(new Color(135,133,129), "Solitude Grey");
        colorNameMap.put(new Color(152,89,36), "Hazelnut Brown");
        colorNameMap.put(new Color(186,109,44), "Ember Orange");
        colorNameMap.put(new Color(216,127,51), "Neon Orange");
        colorNameMap.put(new Color(114,67,27), "Clay Brown");
        colorNameMap.put(new Color(125,53,152), "Cadmium Purple");
        colorNameMap.put(new Color(153,65,186), "Sweet Purple");
        colorNameMap.put(new Color(178,76,216), "Wizard Purple");
        colorNameMap.put(new Color(94,40,114), "Portal Dark Purple");
        colorNameMap.put(new Color(72,108,152), "Modesty Blue");
        colorNameMap.put(new Color(88,132,186), "Melodious Blue");
        colorNameMap.put(new Color(102,153,216), "Siren Blue");
        colorNameMap.put(new Color(54,81,114), "Authority Dark Blue");
        colorNameMap.put(new Color(161,161,36), "Citron Yellow");
        colorNameMap.put(new Color(197,197,44), "Zest Yellow");
        colorNameMap.put(new Color(229,229,51), "Lemon Yellow");
        colorNameMap.put(new Color(121,121,27), "Olive Green");
        colorNameMap.put(new Color(89,144,17), "Mystic Green");
        colorNameMap.put(new Color(109,176,21), "Envy Green");
        colorNameMap.put(new Color(127,204,25), "Luminous Lime");
        colorNameMap.put(new Color(67,108,13), "Sublime Dark Green");
        colorNameMap.put(new Color(170,89,116), "Suspicion Pink");
        colorNameMap.put(new Color(208,109,142), "Wild Pink");
        colorNameMap.put(new Color(242,127,165), "Hot Pink");
        colorNameMap.put(new Color(128,67,87), "Wealth Dark Pink");
        colorNameMap.put(new Color(53,53,53), "Pit Dark Grey");
        colorNameMap.put(new Color(65,65,65), "Sword Grey");
        colorNameMap.put(new Color(76,76,76), "Statue Grey");
        colorNameMap.put(new Color(40,40,40), "Shadow Dark Grey");
        colorNameMap.put(new Color(108,108,108), "Stability Grey");
        colorNameMap.put(new Color(132,132,132), "Grandfather Grey");
        colorNameMap.put(new Color(153,153,153), "Dolphin Grey");
        colorNameMap.put(new Color(81,81,81), "Pigeon Grey");
        colorNameMap.put(new Color(53,89,108), "Dragon Blue");
        colorNameMap.put(new Color(65,109,132), "Dominion Blue");
        colorNameMap.put(new Color(76,127,153), "Nobility Blue");
        colorNameMap.put(new Color(40,67,81), "Jeans Dark Blue");
        colorNameMap.put(new Color(89,44,125), "Mastery Purple");
        colorNameMap.put(new Color(109,54,153), "Circus Purple");
        colorNameMap.put(new Color(127,63,178), "Reagent Purple");
        colorNameMap.put(new Color(67,33,94), "Majestic Dark Purple");
        colorNameMap.put(new Color(36,53,125), "Royal Blue");
        colorNameMap.put(new Color(44,65,153), "Blueberry Blue");
        colorNameMap.put(new Color(51,76,178), "Venomous Blue");
        colorNameMap.put(new Color(27,40,94), "Space Dark Blue");
        colorNameMap.put(new Color(72,53,36), "Smooth Brown");
        colorNameMap.put(new Color(88,65,44), "Wooded Brown");
        colorNameMap.put(new Color(102,76,51), "Legion Brown");
        colorNameMap.put(new Color(54,40,27), "Bark Brown");
        colorNameMap.put(new Color(72,89,36), "Wallow Green");
        colorNameMap.put(new Color(88,109,44), "Fey Green");
        colorNameMap.put(new Color(102,127,51), "Undergrowth Green");
        colorNameMap.put(new Color(54,67,27), "Decadence Green");
        colorNameMap.put(new Color(108,36,36), "Liver Red");
        colorNameMap.put(new Color(132,44,44), "Valorous Red");
        colorNameMap.put(new Color(153,51,51), "Carmine Red");
        colorNameMap.put(new Color(81,27,27), "Merlot Dark Red");
        colorNameMap.put(new Color(17,17,17), "Wrath Black");
        colorNameMap.put(new Color(21,21,21), "Unbearable Black ");
        colorNameMap.put(new Color(25,25,25), "Sin Black");
        colorNameMap.put(new Color(13,13,13), "Endless Black");
        colorNameMap.put(new Color(176,168,54), "Brass Yellow");
        colorNameMap.put(new Color(215,205,66), "Soar Yellow");
        colorNameMap.put(new Color(250,238,77), "Dazzle Yellow");
        colorNameMap.put(new Color(132,126,40), "Pickle Yellow");
        colorNameMap.put(new Color(64,154,150), "Tranquility Blue");
        colorNameMap.put(new Color(79,188,183), "Sparkling Blue");
        colorNameMap.put(new Color(92,219,213), "Brilliant Blue");
        colorNameMap.put(new Color(48,115,112), "Myrtle Dark Blue");
        colorNameMap.put(new Color(52,90,180), "Cerulean Blue");
        colorNameMap.put(new Color(63,110,220), "Horizon Blue");
        colorNameMap.put(new Color(74,128,255), "Prismatic Blue");
        colorNameMap.put(new Color(39,67,135), "Cornflower Dark Blue");
        colorNameMap.put(new Color(0,153,40), "Junior Green");
        colorNameMap.put(new Color(0,187,50), "Vibrant Green");
        colorNameMap.put(new Color(0,217,58), "Emerald Green");
        colorNameMap.put(new Color(0,114,30), "Jade Green");
        colorNameMap.put(new Color(91,60,34), "Stump Brown");
        colorNameMap.put(new Color(111,74,42), "Cocoa Brown");
        colorNameMap.put(new Color(129,86,49), "Burnt Brown");
        colorNameMap.put(new Color(68,45,25), "Nostalgia Dark Brown");
        colorNameMap.put(new Color(79,1,0), "Congealed Dark Red");
        colorNameMap.put(new Color(96,1,0), "Crimson Red");
        colorNameMap.put(new Color(112,2,0), "Heart Red");
        colorNameMap.put(new Color(59,1,0), "Forgotten Dark Red");
        colorNameMap.put(new Color(147,124,113), "Foundation Brown");
        colorNameMap.put(new Color(180,152,138), "Mediocrity Brown");
        colorNameMap.put(new Color(209,177,161), "Complexion Pink");
        colorNameMap.put(new Color(110,93,85), "Wendigo Brown");
        colorNameMap.put(new Color(112,57,25), "Caramel Dark Orange");
        colorNameMap.put(new Color(137,70,31), "Boiling Orange");
        colorNameMap.put(new Color(159,82,36), "Copper Orange");
        colorNameMap.put(new Color(84,43,19), "Knowledge Dark Orange");
        colorNameMap.put(new Color(105,61,76), "Burgundy Purple");
        colorNameMap.put(new Color(128,75,93), "Mulberry Purple");
        colorNameMap.put(new Color(149,87,108), "Impatience Pink");
        colorNameMap.put(new Color(78,46,57), "Subtlety Dark Purple");
        colorNameMap.put(new Color(79,76,97), "Dilapidated Purple");
        colorNameMap.put(new Color(96,93,119), "Pale Purple");
        colorNameMap.put(new Color(112,108,138), "Symptom Purple");
        colorNameMap.put(new Color(59,57,73), "Arsenic Dark Purple");
        colorNameMap.put(new Color(131,93,25), "Striking Yellow");
        colorNameMap.put(new Color(160,114,31), "Infection Yellow");
        colorNameMap.put(new Color(186,133,36), "Mustard Yellow");
        colorNameMap.put(new Color(98,70,19), "Cow Brown");
        colorNameMap.put(new Color(72,82,37), "Eucalypt Green");
        colorNameMap.put(new Color(88,100,45), "Virulent Green");
        colorNameMap.put(new Color(103,117,53), "Algae Green");
        colorNameMap.put(new Color(54,61,28), "Reservation Dark Green");
        colorNameMap.put(new Color(112,54,55), "Forbidden Red");
        colorNameMap.put(new Color(138,66,67), "Cordovan Red");
        colorNameMap.put(new Color(160,77,78), "Wanderlust Red");
        colorNameMap.put(new Color(84,40,41), "Mortician Dark Red");
        colorNameMap.put(new Color(40,28,24), "Malevolence Dark Brown");
        colorNameMap.put(new Color(49,35,30), "Mission Brown");
        colorNameMap.put(new Color(57,41,35), "Earthen Brown");
        colorNameMap.put(new Color(30,21,18), "Chocolate Black");
        colorNameMap.put(new Color(95,75,69), "Mesa Brown");
        colorNameMap.put(new Color(116,92,84), "Gruel Brown");
        colorNameMap.put(new Color(135,107,98), "Warm Brown");
        colorNameMap.put(new Color(71,56,51), "Sturdy Brown");
        colorNameMap.put(new Color(61,64,64), "Abyssal Dark Grey");
        colorNameMap.put(new Color(75,79,79), "Rugged Grey");
        colorNameMap.put(new Color(87,92,92), "Gunmetal Grey");
        colorNameMap.put(new Color(46,48,48), "Stormcloud Dark Grey");
        colorNameMap.put(new Color(86,51,62), "Wildflower Purple");
        colorNameMap.put(new Color(105,62,75), "Solemn Purple");
        colorNameMap.put(new Color(122,73,88), "Madness Purple");
        colorNameMap.put(new Color(64,38,46), "Power Dark Purple");
        colorNameMap.put(new Color(53,43,64), "Oblivion Dark Purple");
        colorNameMap.put(new Color(65,53,79), "Pleasant Purple");
        colorNameMap.put(new Color(76,62,92), "Pollution Purple");
        colorNameMap.put(new Color(40,32,48), "Magician Dark Purple");
        colorNameMap.put(new Color(53,35,24), "Hidden Brown");
        colorNameMap.put(new Color(65,43,30), "Buffalo Brown");
        colorNameMap.put(new Color(76,50,35), "Framing Brown");
        colorNameMap.put(new Color(40,26,18), "Sinister Dark Brown");
        colorNameMap.put(new Color(53,57,29), "Murky Green");
        colorNameMap.put(new Color(65,70,36), "Swamp Green");
        colorNameMap.put(new Color(76,82,42), "Military Green");
        colorNameMap.put(new Color(40,43,22), "Pine Dark Green");
        colorNameMap.put(new Color(100,42,32), "Tomato Red");
        colorNameMap.put(new Color(122,51,39), "Garnet Red");
        colorNameMap.put(new Color(142,60,46), "Chestnut Red");
        colorNameMap.put(new Color(75,31,24), "Unstable Dark Red");
        colorNameMap.put(new Color(26,15,11), "Mysterious Dark Brown");
        colorNameMap.put(new Color(31,18,13), "Rigid Dark Brown");
        colorNameMap.put(new Color(37,22,16), "Ponderous Dark Brown");
        colorNameMap.put(new Color(19,11,8), "Truthful Black");
        colorNameMap.put(new Color(133,33,34), "Vampiric Red");
        colorNameMap.put(new Color(163,41,42), "Firestorm Red");
        colorNameMap.put(new Color(189,48,49), "Rage Red");
        colorNameMap.put(new Color(100,25,25), "Suffering Dark Red");
        colorNameMap.put(new Color(104,44,68), "Hazy Purple");
        colorNameMap.put(new Color(127,54,83), "Vintner Pink");
        colorNameMap.put(new Color(148,63,97), "Rosey Pink");
        colorNameMap.put(new Color(78,33,51), "Plum Purple");
        colorNameMap.put(new Color(64,17,20), "Secret Dark Red");
        colorNameMap.put(new Color(79,21,25), "Lusty Dark Red");
        colorNameMap.put(new Color(92,25,29), "Smitten Red");
        colorNameMap.put(new Color(48,13,15), "Mahogany Dark Red");
        colorNameMap.put(new Color(15,88,94), "Ocean Blue");
        colorNameMap.put(new Color(18,108,115), "Sulphate Blue");
        colorNameMap.put(new Color(22,126,134), "Teal Blue");
        colorNameMap.put(new Color(11,66,70), "Spirit Dark Green");
        colorNameMap.put(new Color(40,100,98), "Depths Green");
        colorNameMap.put(new Color(50,122,120), "Longing Green");
        colorNameMap.put(new Color(58,142,140), "Sapphire Blue");
        colorNameMap.put(new Color(30,75,74), "Weave Dark Green");
        colorNameMap.put(new Color(60,31,43), "Temptress Purple");
        colorNameMap.put(new Color(74,37,53), "Chasm Purple");
        colorNameMap.put(new Color(86,44,62), "Puce Purple");
        colorNameMap.put(new Color(45,23,32), "Ultimatum Dark Purple");
        colorNameMap.put(new Color(14,127,93), "Refreshment Green");
        colorNameMap.put(new Color(17,155,114), "Island Green");
        colorNameMap.put(new Color(20,180,133), "Turquoise Green");
        colorNameMap.put(new Color(10,95,70), "Beryl Dark Green");
        colorNameMap.put(new Color(70,70,70), "Distance Dark Grey");
        colorNameMap.put(new Color(86,86,86), "Machine Grey");
        colorNameMap.put(new Color(100,100,100), "Granite Grey");
        colorNameMap.put(new Color(52,52,52), "Jet Dark Grey");
        colorNameMap.put(new Color(152,123,103), "Beaver Brown");
        colorNameMap.put(new Color(186,150,126), "Taupe Brown");
        colorNameMap.put(new Color(216,175,147), "Tan Pink");
        colorNameMap.put(new Color(114,92,77), "Robust Brown");
        colorNameMap.put(new Color(89,117,105), "Deception Green");
        colorNameMap.put(new Color(109,144,129), "Occult Green");
        colorNameMap.put(new Color(127,167,150), "Seasick Green");
        colorNameMap.put(new Color(67,88,79), "Sleek Dark Green");
        
    }

}

