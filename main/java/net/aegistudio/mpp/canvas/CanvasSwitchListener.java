/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package net.aegistudio.mpp.canvas;

// import net.aegistudio.mcinject.network.PacketPlayOutSetSlot;
// import net.aegistudio.mcinject.network.PlayerConnection;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class CanvasSwitchListener
implements Listener {
    private final MapPainting painting;

    public CanvasSwitchListener(MapPainting painting) {
        this.painting = painting;
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onSwitchItem(PlayerItemHeldEvent event) {
        if (event.isCancelled()) {
            return;
        }
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        int mapId = this.painting.m_canvasManager.scopeListener.parse(item);
        if (mapId < 0) {
            return;
        }
        // ItemStack pseudoItem = item.clone();
        // pseudoItem.setType(Material.PAINTING);
        // this.painting.getServer().getScheduler().runTaskLater((Plugin)this.painting, () -> new PlayerConnection(this.painting.inject, event.getPlayer()).sendPacket(new PacketPlayOutSetSlot(this.painting.inject, 0, event.getNewSlot() + 36, pseudoItem)), 3L);
    }
}

