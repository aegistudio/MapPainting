package net.aegistudio.mpp.inject;

import org.bukkit.entity.Player;

public interface PacketSender {
	public <Packet> void sendPacket(Player player, Packet packet);
}
