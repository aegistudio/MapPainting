package net.aegistudio.mpp.export;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;

/**
 * <p>A canvas is said to be place-sensitive if the canvas
 * should be notified about where it is placed and when
 * it is removed, what is spawned.</p>
 * 
 * <p>With this interface, you could:
 * <li>1. know whenever and wherever this canvas is placed.
 * <li>2. set spawned item when this canvas is unplaced.
 * <li>3. additional operation whenever it is placed or removed.</p>
 * 
 * @author aegistudio
 */

public interface PlaceSensitive {
	/**
	 * After this canvas is placed, you will know where is the canvas placed
	 * and at which place is it placed.
	 * 
	 * @param blockLocation the canvas adhering block
	 * @param blockFace at which face is the canvas placed
	 */
	public void place(Location blockLocation, BlockFace blockFace);
	
	/**
	 * After the canvas is destroyed, you get a chance to control its spawning
	 * item.
	 * 
	 * @param spawnedItem 
	 */
	public void unplace(Item spawnedItem);
}
