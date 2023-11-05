/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.algo;

public class StringLineGenerator
implements StringGenerator {
    CharacterGenerator font;

    public StringLineGenerator(CharacterGenerator cgen) {
        this.font = cgen;
    }

    @Override
    public int string(Paintable p, int x, int y, float scale, String content) {
        int offset = 0;
        for (char c : content.toCharArray()) {
            offset += this.font.chargen(p, x + offset, y, scale, c) + 1;
        }
        return offset;
    }
}

