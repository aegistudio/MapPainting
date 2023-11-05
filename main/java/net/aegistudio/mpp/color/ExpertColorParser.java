
package net.aegistudio.mpp.color;

import java.util.Map;
import java.util.TreeMap;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.configuration.ConfigurationSection;

public class ExpertColorParser
implements ColorParser {
    public final TreeMap<String, PseudoColor> colors = new TreeMap();

    public ExpertColorParser() {
        this.colors.put("red", new PseudoColor(255, 0, 0));
        this.colors.put("green", new PseudoColor(0, 255, 0));
        this.colors.put("blue", new PseudoColor(0, 0, 255));
        this.colors.put("cyan", new PseudoColor(0, 255, 255));
        this.colors.put("magenta", new PseudoColor(255, 0, 255));
        this.colors.put("yellow", new PseudoColor(255, 255, 0));
        this.colors.put("black", new PseudoColor(0, 0, 0));
        this.colors.put("white", new PseudoColor(255, 255, 255));
        this.colors.put("transparent", new PseudoColor());
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        this.colors.clear();
        for (String colorName : section.getKeys(false)) {
            PseudoColor colorValue = new PseudoColor();
            colorValue.load(section.getConfigurationSection(colorName));
            this.colors.put(colorName, colorValue);
        }
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
        for (Map.Entry<String, PseudoColor> colorEntry : this.colors.entrySet()) {
            if (!section.contains(colorEntry.getKey())) {
                section.createSection(colorEntry.getKey());
            }
            colorEntry.getValue().save(section.getConfigurationSection(colorEntry.getKey()));
        }
    }

    @Override
    public PseudoColor parseColor(String input) {
        return this.colors.get(input.toLowerCase());
    }
}

