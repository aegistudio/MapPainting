package net.aegistudio.mpp.algo;

public class DdaLineGenerator implements LineGenerator {
	
	@Override
	public void line(Paintable p, int x1, int x2, int y1, int y2) {
		double dy = y2 - y1;
		double dx = x2 - x1;
		
		// Using dda.
		if(dx != 0 || dy != 0) 
			if(Math.abs(dy) >= Math.abs(dx)) {
				int beginX, beginY;
				
				if(dy <= 0) {
					beginX = x2;
					beginY = y2;
				}
				else {
					beginX = x1;
					beginY = y1;
				}
				
				double diff = dx / dy;
				for(int i = 0; i < Math.abs(dy); i ++) 
					p.set((int) Math.round(beginX + diff * i), beginY + i);
			}
			else {
				int beginX, beginY;
				
				if(dx <= 0) {
					beginX = x2;
					beginY = y2;
				}
				else {
					beginX = x1;
					beginY = y1;
				}
				
				double diff = dy / dx;
				for(int i = 0; i < Math.abs(dx); i ++) 
					p.set(beginX + i, (int) Math.round(beginY + diff * i));
			}
	}
}
