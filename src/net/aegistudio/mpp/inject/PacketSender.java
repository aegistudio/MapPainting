package net.aegistudio.mpp.inject;

import org.bukkit.entity.Player;

public interface PacketSender {
	public Object getHandle(Player player);
	
	public <Packet> void sendPacket(Player player, Packet packet);
}
