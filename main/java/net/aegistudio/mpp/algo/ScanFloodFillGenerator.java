package net.aegistudio.mpp.algo;

import java.awt.Color;

/**
 * Default implementation of filling, using scan flood fill algorithm.
 *
 * @author aegistudio
 */

public class ScanFloodFillGenerator implements FillGenerator {

    @Override
    public void fill(Paintable p, int x, int y, Color fill) {
        Color seed = p.get(x, y);
        p.color(fill);
        this.seedFill(p, x, y, fill, seed);
    }

    private void seedFill(Paintable pa, int x, int y, Color fill, Color seed) {
        if(fill == null || seed == null) return;
        if(fill.getRGB() == seed.getRGB()) return;
        if(!inRange(pa, x, y)) return;

        pa.set(x, y);
        fill = pa.get(x, y);

        int xmin = x - 1;
        for(; xmin >= 0; xmin --) {
            Color color = pa.get(xmin, y);
            if(color == null) break;
            if(color.getRGB() != seed.getRGB()) break;
            pa.set(xmin, y);
        }

        int xmax = x + 1;
        for(; xmax <= pa.width(); xmax ++) {
            Color color = pa.get(xmax, y);
            if(color == null) break;
            if(color.getRGB() != seed.getRGB()) break;
            pa.set(xmax, y);
        }

        for(int p = xmin + 1; p < xmax; p ++) {
            Color up = pa.get(p, y + 1);
            if(up != null) {
                if(up.getRGB() == seed.getRGB())
                    seedFill(pa, p, y + 1, fill, seed);
            }

            Color down = pa.get(p, y - 1);
            if(down != null) {
                if(down.getRGB() == seed.getRGB())
                    seedFill(pa, p, y - 1, fill, seed);
            }
        }
    }

    private boolean inRange(Paintable p, int i, int j) {
        if(i < 0) return false;
        if(j < 0) return false;
        if(i >= p.width()) return false;
        if(j >= p.height()) return false;
        return true;
    }
}

