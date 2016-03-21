package net.aegistudio.mpp;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class Interaction {
	public final int x, y;
	public final CommandSender sender;
	public final Location blockLocation;
	public final Location frameLocation;
	
	public Interaction(int x, int y, CommandSender sender, Location blockLocation, Location frameLocation) {
		this.x = x;
		this.y = y;
		this.blockLocation = blockLocation;
		this.frameLocation = frameLocation;
		this.sender = sender;
	}
	
	public Interaction reCoordinate(int x, int y) {
		return new Interaction(x, y, this.sender, this.blockLocation, frameLocation);
	}
}
