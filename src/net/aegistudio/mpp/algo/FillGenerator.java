package net.aegistudio.mpp.algo;

import java.awt.Color;

import net.aegistudio.mpp.export.Asset;

public interface FillGenerator extends Asset {
	public void fill(Paintable p, int x, int y, Color fill);
}
