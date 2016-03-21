package net.aegistudio.mpp.script;

public class Region {
	public int x1, y1, x2, y2;
	
	public Region(int x1, int y1, int x2, int y2) {
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}
	
	public boolean inside(int x, int y) {
		return inside(x1, x2, x) && inside(y1, y2, y);
	}
	
	private boolean inside(int a, int b, int c) {
		return Math.min(a, b) <= c && Math.max(a, b) >= c;
	}
}
