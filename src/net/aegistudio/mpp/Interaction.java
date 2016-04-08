package net.aegistudio.mpp;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * The interaction detail, when the player paint on
 * or interact with a map canvas.
 * 
 * @author aegistudio
 */

public class Interaction {
	/** The x and y coordinate. */
	public final int x, y;
	
	/** The command sender. */
	public final CommandSender sender;
	
	/** The location of block that the map canvas adhering to. */
	public final Location blockLocation;
	
	/** The location of item frame where the canvas placed.**/
	public final Location frameLocation;
	
	/** Does the player use right hand to interact with? **/
	public final boolean rightHanded;
	
	/**
	 * Construct an interaction.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param sender who invoke this interaction.
	 * @param blockLocation the block that the map canvas adhering to.
	 * @param frameLocation the location of the item frame.
	 * @param rightHanded does the player use right hand to interact with?
	 */
	
	public Interaction(int x, int y, CommandSender sender, Location blockLocation, Location frameLocation, boolean rightHanded) {
		this.x = x;
		this.y = y;
		this.blockLocation = blockLocation;
		this.frameLocation = frameLocation;
		this.sender = sender;
		this.rightHanded = rightHanded;
	}
	
	/**
	 * Clone this interaction, while changing the place where interaction happened.
	 * @param x the new x coordinate.
	 * @param y the new y coordinate.
	 * @return the re-coordinated instance.
	 */
	public Interaction reCoordinate(int x, int y) {
		return new Interaction(x, y, this.sender, this.blockLocation, frameLocation, rightHanded);
	}
}
