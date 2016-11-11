package net.aegistudio.mpp.color;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import net.aegistudio.mpp.MapPainting;

public class ExpertColorParser implements ColorParser {
	public final TreeMap<String, PseudoColor> colors = new TreeMap<String, PseudoColor>(); {
		colors.put("red", new PseudoColor(255, 0, 0));
		colors.put("green", new PseudoColor(0, 255, 0));
		colors.put("blue", new PseudoColor(0, 0, 255));
		colors.put("cyan", new PseudoColor(0, 255, 255));
		colors.put("magenta", new PseudoColor(255, 0, 255));
		colors.put("yellow", new PseudoColor(255, 255, 0));
		colors.put("black", new PseudoColor(0, 0, 0));
		colors.put("white", new PseudoColor(255, 255, 255));
		colors.put("transparent", new PseudoColor());
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		this.colors.clear();
		for(String colorName : section.getKeys(false)) {
			PseudoColor colorValue = new PseudoColor();
			colorValue.load(section.getConfigurationSection(colorName));
			this.colors.put(colorName, colorValue);
		}
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {
		for(Entry<String, PseudoColor> colorEntry : this.colors.entrySet()) {
			if(!section.contains(colorEntry.getKey())) section.createSection(colorEntry.getKey());
			colorEntry.getValue().save(section.getConfigurationSection(colorEntry.getKey()));
		}
	}

	@Override
	public PseudoColor parseColor(String input) {
		return colors.get(input.toLowerCase());
	}
}
