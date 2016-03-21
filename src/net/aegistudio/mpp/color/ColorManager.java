package net.aegistudio.mpp.color;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class ColorManager implements Module, ColorParser {
	public Map<String, ColorParser> parsers = new TreeMap<String, ColorParser>();
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		for(Entry<String, ColorParser> parser : parsers.entrySet()) {
			if(section.contains(parser.getKey())) 
				parser.getValue().load(painting, section.getConfigurationSection(parser.getKey()));
			else parser.getValue().save(painting, section.createSection(parser.getKey()));
		}
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {
		
	}

	@Override
	public PseudoColor parseColor(String input) throws RuntimeException {
		for(Entry<String, ColorParser> parser : parsers.entrySet()) {
			PseudoColor color = parser.getValue().parseColor(input);
			if(color != null) return color;
		}
		return null;
	}
}
