package net.aegistudio.mpp.algo;

import java.awt.Color;

public interface FillGenerator extends Generator {
	public void fill(Paintable p, int x, int y, Color fill);
}
