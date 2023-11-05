/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.color;

import net.aegistudio.mpp.MapPainting;
import org.bukkit.configuration.ConfigurationSection;

public class RgbColorParser
implements ColorParser {
    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }

    @Override
    public PseudoColor parseColor(String input) throws RuntimeException {
        String rawRgb = null;
        if (input.startsWith("#")) {
            rawRgb = input.substring(1);
        }
        if (input.startsWith("RGB")) {
            rawRgb = input.substring(3);
        }
        if (rawRgb == null || rawRgb.length() == 0) {
            return null;
        }
        if (rawRgb.length() % 3 != 0) {
            return null;
        }
        int PerToken = rawRgb.length() / 3;
        StringBuilder builder = new StringBuilder("0");
        for (int i = 0; i < PerToken; ++i) {
            builder.append('f');
        }
        return new PseudoColor(Integer.parseInt("0" + rawRgb.substring(0, PerToken), 16), Integer.parseInt("0" + rawRgb.substring(PerToken, 2 * PerToken), 16), Integer.parseInt("0" + rawRgb.substring(2 * PerToken), 16), Integer.parseInt(new String(builder), 16));
    }
}

