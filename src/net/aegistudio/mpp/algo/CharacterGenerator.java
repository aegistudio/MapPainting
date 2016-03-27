package net.aegistudio.mpp.algo;

/**
 * Generate specified bitmap.
 * 
 * @author aegistudio
 */

public interface CharacterGenerator {
	public void chargen(Paintable p, int x1, int y1, int x2, int y2, char c);
	
	public int chargen(Paintable p, int x, int y, float scale, char c);
}
