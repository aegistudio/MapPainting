package net.aegistudio.mpp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InteractInfo {
	public final int x, y;
	public final Player player;
	public final Location blockLocation;
	
	public InteractInfo(int x, int y, Player player, Location blockLocation) {
		this.x = x;
		this.y = y;
		this.blockLocation = blockLocation;
		this.player = player;
	}
	
	public InteractInfo reCoordinate(int x, int y) {
		return new InteractInfo(x, y, this.player, this.blockLocation);
	}
}
