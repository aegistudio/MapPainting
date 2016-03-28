package net.aegistudio.mpp.algo;

import org.bukkit.map.MapFont;

public class RightAlignStringGenerator implements StringGenerator {
	MapFont font;
	StringLineGenerator generator;
	public RightAlignStringGenerator(StringLineGenerator lgen, MapFont font) {
		this.font = font;
		this.generator = lgen;
	}
	
	@Override
	public int string(Paintable p, int x, int y, float scale, String content) {
		int totalLength = 0;
		for(char ch : content.toCharArray())
			totalLength += ((font.getChar(ch).getWidth() * scale) + 1);
		generator.string(p, x - totalLength, y, scale, content);
		return totalLength;
	}
}
