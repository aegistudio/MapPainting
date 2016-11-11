package net.aegistudio.mpp.palette;

import java.awt.Color;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class PaletteManager implements Module {
	public static final String IDENTIFIER = "identifier";
	public String identifier = "@palette.identifier";

	//public static final String ITEMNAME = "itemName";
	//public String itemName = ChatColor.RESET + "Palette";
	
	public static final String CYAN = "cyan";
	public String cyan = "@palette.cyan";
	
	public static final String MAGENTA = "magenta";
	public String magenta = "@palette.magenta";
	
	public static final String YELLOW = "yellow";
	public String yellow = "@palette.yellow";

	// Open for the call of using dye!
	public NaivePigmentRecipe dye;
	
	// Open for the call of using paint bucket!
	public PaintBucketRecipe paintBucket;
	
	public Color getColor(ItemStack item) {
		boolean hasIdentifier = false;
		int cyan = 0;	int magenta = 0;	int yellow = 0;
		
		if(item.getItemMeta().hasLore())
			for(String lore : item.getItemMeta().getLore()) {
				if(lore.equals(this.identifier)) hasIdentifier = true;
				if(lore.startsWith(this.cyan)) cyan = Integer.parseInt(lore.substring(this.cyan.length()));
				if(lore.startsWith(this.magenta)) magenta = Integer.parseInt(lore.substring(this.magenta.length()));
				if(lore.startsWith(this.yellow)) yellow = Integer.parseInt(lore.substring(this.yellow.length()));
			}
		return hasIdentifier? new Color(255 - cyan, 255 - magenta, 255 - yellow) : null;
	}
	
	public void setColor(ItemStack item, Color color) {
		ItemMeta meta = item.getItemMeta();
		ArrayList<String> newLore = new ArrayList<String>();
		if(meta.hasLore())
			for(String lore : meta.getLore()) {
				if(lore.equals(IDENTIFIER)) continue;
				if(lore.startsWith(this.cyan)) continue;
				if(lore.startsWith(this.magenta)) continue;
				if(lore.startsWith(this.yellow)) continue;
				newLore.add(lore);
			}
		
		newLore.add(identifier);
		newLore.add(cyan + (255 - color.getRed()));
		newLore.add(magenta + (255 - color.getGreen()));
		newLore.add(yellow + (255 - color.getBlue()));
		
		meta.setLore(newLore);
		item.setItemMeta(meta);
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		// Load configuration.
		identifier = painting.getLocale(IDENTIFIER, identifier, section);
		cyan = painting.getLocale(CYAN, cyan, section);
		magenta = painting.getLocale(MAGENTA, magenta, section);
		yellow = painting.getLocale(YELLOW, yellow, section);
		
		// Add naive pigment recipes and listener.
		this.dye = new NaivePigmentRecipe();
		this.dye.load(painting, section);
		
		// Add paint bucket recipes and listener.
		this.paintBucket = new PaintBucketRecipe();
		this.paintBucket.load(painting, section);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	}
}
