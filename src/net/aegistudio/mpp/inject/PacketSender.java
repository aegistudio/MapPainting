package net.aegistudio.mpp.inject;

import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.canvas.Graphic;

public interface PacketSender {
	public void sendMapPacket(Player player, MapView view, Graphic graphic);
}
