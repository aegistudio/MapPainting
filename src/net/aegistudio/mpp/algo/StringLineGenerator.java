package net.aegistudio.mpp.algo;

public class StringLineGenerator implements StringGenerator {
	CharacterGenerator cgen;
	public StringLineGenerator(CharacterGenerator cgen) {
		this.cgen = cgen;
	}
	
	@Override
	public void string(Paintable p, int x, int y, float scale, String content) {
		int offset = 0;
		for(char c : content.toCharArray())
			offset += (cgen.chargen(p, x + offset, y, scale, c) + 1);
	}
}
