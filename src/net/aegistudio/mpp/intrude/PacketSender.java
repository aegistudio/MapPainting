package net.aegistudio.mpp.intrude;

import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.canvas.Graphic;

public interface PacketSender {
	public void sendPacket(Player player, MapView view, Graphic graphic);
}
