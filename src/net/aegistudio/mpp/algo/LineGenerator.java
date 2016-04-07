package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.export.Asset;

public interface LineGenerator extends Asset {
	public void line(Paintable p, int x1, int y1, int x2, int y2);
}
