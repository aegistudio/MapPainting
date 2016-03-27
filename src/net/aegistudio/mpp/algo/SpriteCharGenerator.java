package net.aegistudio.mpp.algo;

import org.bukkit.map.MapFont;
import org.bukkit.map.MapFont.CharacterSprite;

public class SpriteCharGenerator implements CharacterGenerator {
	public final MapFont font;
	public SpriteCharGenerator(MapFont font) {
		this.font = font;
	}
	
	@Override
	public void chargen(Paintable p, int x, int y, int width, int height, char c) {
		CharacterSprite sprite = font.getChar(c);
		
		for(int i = 0; i < height; i ++)
			for(int j = 0; j < width; j ++)	{
				int row = (int) (((double) i) / height * sprite.getHeight());
				int col = (int) (((double) j) / width * sprite.getWidth());
				if(sprite.get(row, col)) 
					p.set(x + j, y + height - 1 - i);
			}
	}

	@Override
	public int chargen(Paintable p, int x, int y, float scale, char c) {
		CharacterSprite sprite = font.getChar(c);
		this.chargen(p, x, y, (int)(sprite.getWidth() * scale), (int)(sprite.getHeight() * scale), c);
		return (int) (sprite.getWidth() * scale);
	}
}
