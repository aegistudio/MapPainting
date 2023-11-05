/*
 * Decompiled with CFR 0.145.
 */
package net.aegistudio.mpp.algo;

public class DdaLineGenerator
implements LineGenerator {
    @Override
    public void line(Paintable p, int x1, int y1, int x2, int y2) {
        block8 : {
            double dy = y2 - y1;
            double dx = x2 - x1;
            if (dx == 0.0 && dy == 0.0) break block8;
            if (Math.abs(dy) >= Math.abs(dx)) {
                int beginX;
                int beginY;
                if (dy <= 0.0) {
                    beginX = x2;
                    beginY = y2;
                } else {
                    beginX = x1;
                    beginY = y1;
                }
                double diff = dx / dy;
                int i = 0;
                while ((double)i <= Math.abs(dy)) {
                    p.set((int)Math.round((double)beginX + diff * (double)i), beginY + i);
                    ++i;
                }
            } else {
                int beginX;
                int beginY;
                if (dx <= 0.0) {
                    beginX = x2;
                    beginY = y2;
                } else {
                    beginX = x1;
                    beginY = y1;
                }
                double diff = dy / dx;
                int i = 0;
                while ((double)i <= Math.abs(dx)) {
                    p.set(beginX + i, (int)Math.round((double)beginY + diff * (double)i));
                    ++i;
                }
            }
        }
    }
}

