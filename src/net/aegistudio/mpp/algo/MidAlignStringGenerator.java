package net.aegistudio.mpp.algo;

/**
 * A center-aligned string generator.
 * 
 * @author aegistudio
 */

public class MidAlignStringGenerator implements StringGenerator {
	StringLineGenerator generator;
	CharacterGenerator font;
	public MidAlignStringGenerator(CharacterGenerator lgen) {
		this.generator = new StringLineGenerator(lgen);
		this.font = lgen;
	}
	
	@Override
	public int string(Paintable p, int x, int y, float scale, String content) {
		int totalLength = 0;
		for(char ch : content.toCharArray())
			totalLength += ((font.metricWidth(scale, ch)) + 1);
		generator.string(p, x - (totalLength >> 1), y, scale, content);
		return totalLength;
	}
}
