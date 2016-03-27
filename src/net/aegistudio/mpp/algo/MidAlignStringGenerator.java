package net.aegistudio.mpp.algo;

import org.bukkit.map.MapFont;

public class MidAlignStringGenerator implements StringGenerator {
	MapFont font;
	StringLineGenerator generator;
	public MidAlignStringGenerator(StringLineGenerator lgen, MapFont font) {
		this.font = font;
		this.generator = lgen;
	}
	
	@Override
	public void string(Paintable p, int x, int y, float scale, String content) {
		int totalLength = 0;
		for(char ch : content.toCharArray())
			totalLength += ((font.getChar(ch).getWidth() * scale) + 1);
		generator.string(p, x - (totalLength >> 1), y, scale, content);
	}
}
