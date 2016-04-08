package net.aegistudio.mpp.algo;

/**
 * A simple, left-aligned string generator.
 * 
 * @author aegistudio
 */

public class StringLineGenerator implements StringGenerator {
	CharacterGenerator font;
	public StringLineGenerator(CharacterGenerator cgen) {
		this.font = cgen;
	}
	
	@Override
	public int string(Paintable p, int x, int y, float scale, String content) {
		int offset = 0;
		for(char c : content.toCharArray())
			offset += (font.chargen(p, x + offset, y, scale, c) + 1);
		return offset;
	}
}
