package net.aegistudio.mpp.canvas;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.mcinject.network.PacketPlayOutSetSlot;
import net.aegistudio.mpp.mcinject.network.PlayerConnection;

public class CanvasSwitchListener implements Listener {
	private final MapPainting painting;
	public CanvasSwitchListener(MapPainting painting) {
		this.painting = painting;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSwitchItem(PlayerItemHeldEvent event) {
		if(event.isCancelled()) return;
		ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
		int mapId = painting.canvas.scopeListener.parse(item);
		if(mapId < 0) return;
		
		ItemStack pseudoItem = item.clone();
		pseudoItem.setType(Material.MAP);
		pseudoItem.setDurability((short) mapId);
		
		painting.getServer().getScheduler().runTaskLater(painting, () -> 
			new PlayerConnection(painting.inject, event.getPlayer())
				.sendPacket(new PacketPlayOutSetSlot(painting.inject, 0, 
						event.getNewSlot() + 36, pseudoItem)), 3);
	}
}
