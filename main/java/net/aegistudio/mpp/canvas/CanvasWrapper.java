package net.aegistudio.mpp.canvas;

import java.util.Set;

public interface CanvasWrapper {
	public void setWrapping(int layer, String newCanvas);
	public void showWrapped(Set<String> container);
}
