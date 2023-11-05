/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.map.MapFont
 *  org.bukkit.map.MapFont$CharacterSprite
 */
package net.aegistudio.mpp.algo;

import org.bukkit.map.MapFont;

public class SpriteCharGenerator
implements CharacterGenerator {
    public final MapFont font;

    public SpriteCharGenerator(MapFont font) {
        this.font = font;
    }

    @Override
    public void chargen(Paintable p, int x, int y, int width, int height, char c) {
        MapFont.CharacterSprite sprite = this.font.getChar(c);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int col;
                int row = (int)((double)i / (double)height * (double)sprite.getHeight());
                if (!sprite.get(row, col = (int)((double)j / (double)width * (double)sprite.getWidth()))) continue;
                p.set(x + j, y + height - 1 - i);
            }
        }
    }

    @Override
    public int chargen(Paintable p, int x, int y, float scale, char c) {
        MapFont.CharacterSprite sprite = this.font.getChar(c);
        this.chargen(p, x, y, (int)((float)sprite.getWidth() * scale), (int)((float)sprite.getHeight() * scale), c);
        return (int)((float)sprite.getWidth() * scale);
    }

    @Override
    public int metricWidth(float scale, char c) {
        return (int)((float)this.font.getChar(c).getWidth() * scale);
    }

    @Override
    public int metricHeight(float scale, char c) {
        return (int)((float)this.font.getChar(c).getHeight() * scale);
    }
}

