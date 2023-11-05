/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.algo;

public class RightAlignStringGenerator
implements StringGenerator {
    CharacterGenerator font;
    StringLineGenerator generator;

    public RightAlignStringGenerator(CharacterGenerator font) {
        this.font = font;
        this.generator = new StringLineGenerator(font);
    }

    @Override
    public int string(Paintable p, int x, int y, float scale, String content) {
        int totalLength = 0;
        for (char ch : content.toCharArray()) {
            totalLength += this.font.metricWidth(scale, ch) + 1;
        }
        this.generator.string(p, x - totalLength, y, scale, content);
        return totalLength;
    }
}

