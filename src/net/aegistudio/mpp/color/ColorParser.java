package net.aegistudio.mpp.color;

import net.aegistudio.mpp.Module;

public interface ColorParser extends Module {
	public PseudoColor parseColor(String input) throws RuntimeException;
}
