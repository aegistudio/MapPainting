/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.color;

import java.awt.Color;
import org.bukkit.configuration.ConfigurationSection;

public class PseudoColor {
    public Color color;

    public PseudoColor() {
    }

    public PseudoColor(int r, int g, int b) {
        this.color = new Color(r, g, b);
    }

    public PseudoColor(int r, int g, int b, int limit) {
        this.color = new Color((int)((double)r / (double) limit * 255.0), (int)((double)g / (double) limit * 255.0), (int)((double)b / (double) limit * 255.0));
    }

    public void load(ConfigurationSection config) {
        if (!config.getBoolean("transparent")) {
            int r = config.getInt("r");
            int g = config.getInt("g");
            int b = config.getInt("b");
            this.color = new Color(r, g, b);
        } else {
            this.color = null;
        }
    }

    public void save(ConfigurationSection config) {
        config.set("transparent", this.color == null);
        if (this.color != null) {
            config.set("r", this.color.getRed());
            config.set("g", this.color.getGreen());
            config.set("b", this.color.getBlue());
        }
    }
}

