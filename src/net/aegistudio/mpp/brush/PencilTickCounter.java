package net.aegistudio.mpp.brush;

public class PencilTickCounter {
	public int x, y, count;
	
	public PencilTickCounter(int x, int y, int count) {
		this.x = x;
		this.y = y;
		this.count = count;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PencilTickCounter other = (PencilTickCounter) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + this.x;
		hash = 61 * hash + this.y;
		return hash;
	}
}
