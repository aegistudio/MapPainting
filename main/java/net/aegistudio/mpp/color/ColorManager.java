/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.color;

import java.util.Map;
import java.util.TreeMap;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;
import org.bukkit.configuration.ConfigurationSection;

public class ColorManager
        implements Module,
        ColorParser {
    public Map<String, ColorParser> parsers = new TreeMap<>();

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        for (Map.Entry<String, ColorParser> parser : this.parsers.entrySet()) {
            if (section.contains(parser.getKey())) {
                parser.getValue().load(painting, section.getConfigurationSection(parser.getKey()));
                continue;
            }
            parser.getValue().save(painting, section.createSection(parser.getKey()));
        }
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }

    @Override
    public PseudoColor parseColor(String input) throws RuntimeException {
        for (Map.Entry<String, ColorParser> parser : this.parsers.entrySet()) {
            PseudoColor color = parser.getValue().parseColor(input);
            if (color == null) continue;
            return color;
        }
        return null;
    }
}

