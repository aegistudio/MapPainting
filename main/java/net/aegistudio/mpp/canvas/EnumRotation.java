package net.aegistudio.mpp.canvas;

/**
 * The rotation of map is very special that the map is too abstract.
 * @author aegistudio
 */

public enum EnumRotation {
	NONE					(+1, 0, 0, +1),
	CLOCKWISE_45			(0, -1, +1, 0),
	CLOCKWISE				(-1, 0, 0, -1),
	CLOCKWISE_135			(0, +1, -1, 0),
	FLIPPED					(+1, 0, 0, +1),
	FLIPPED_45				(0, -1, +1, 0),
	COUNTER_CLOCKWISE		(-1, 0, 0, -1),
	COUNTER_CLOCKWISE_45	(0, +1, -1, 0);
	
	public final double x1, y1, x2, y2;
	private EnumRotation(double x1, double y1, double x2, double y2) {
		this.x1 = x1;	this.y1 = y1;
		this.x2 = x2;	this.y2 = y2;
	}
	
	public double u(double u, double v) {
		return x1 * u + y1 * v;
	}
	
	public double v(double u, double v) {
		return x2 * u + y2 * v;
	}
}
