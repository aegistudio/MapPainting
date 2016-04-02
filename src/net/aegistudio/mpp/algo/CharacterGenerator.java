package net.aegistudio.mpp.algo;

import net.aegistudio.mpp.Asset;

/**
 * Generate specified bitmap.
 * 
 * @author aegistudio
 */

public interface CharacterGenerator extends Asset {
	public void chargen(Paintable p, int x1, int y1, int x2, int y2, char c);
	
	public int chargen(Paintable p, int x, int y, float scale, char c);
}
