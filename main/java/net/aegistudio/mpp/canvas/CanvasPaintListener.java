package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.MapMeta;

import java.util.Collection;
import java.util.function.BiFunction;

public class CanvasPaintListener implements Listener {
    private final MapPainting painting;

    public CanvasPaintListener(MapPainting painting) {
        this.painting = painting;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent attack) {
        if (!(attack.getEntity() instanceof ItemFrame)) return;
        if (!(attack.getDamager() instanceof Player)) return;

        ItemFrame itemFrame = (ItemFrame) attack.getEntity();
        if (itemFrame.getItem().getType() != Material.FILLED_MAP) return;
        int mapId = (int) ((MapMeta) itemFrame.getItem().getItemMeta()).getMapId();

        MapCanvasRegistry registry = painting.m_canvasManager.idCanvasMap.get(mapId);
        if (registry == null) return;

        Player player = (Player) attack.getDamager();
        if (manipulate(itemFrame, registry, player, false))
            attack.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent interact) {
        Collection<Entity> entities;
        Location center;
        Location boxSize;

        Location playerEyePos = interact.getPlayer().getLocation();
        double yaw = (playerEyePos.getYaw() + 90) * Math.PI / 180;
        double pitch = -playerEyePos.getPitch() * Math.PI / 180;

        double a = Math.cos(yaw) * Math.cos(pitch);
        double b = Math.sin(pitch);
        double c = Math.sin(yaw) * Math.cos(pitch);


        if (interact.getClickedBlock() != null) {
            Location blockCenter = interact.getClickedBlock().getLocation().add(0.5, 0.5, 0.5).clone();

            center = playerEyePos.clone().add(blockCenter).multiply(0.5);
            boxSize = playerEyePos.clone().multiply(-1).add(blockCenter);
        } else {
            center = playerEyePos.clone().add(a * 2, b * 2, c * 2);
            boxSize = new Location(playerEyePos.getWorld(), a * 4, b * 4, c * 4);
        }

        entities = center.getWorld().getNearbyEntities(
                center, Math.abs(boxSize.getX()) + 1, Math.abs(boxSize.getY()) + 1, Math.abs(boxSize.getZ()) + 1);

        for (Entity entity : entities)
            if (entity instanceof ItemFrame) {
                // Check containing map.
                ItemFrame itemFrame = (ItemFrame) entity;
                if (itemFrame.getItem().getType() != Material.FILLED_MAP) continue;

                // Check vector.
                int vecFrameX = itemFrame.getFacing().getModX();
                int vecFrameZ = itemFrame.getFacing().getModZ();
                if (vecFrameX * a + vecFrameZ * c > 0) continue;

                int mapId = (int) ((MapMeta) itemFrame.getItem().getItemMeta()).getMapId();
                MapCanvasRegistry registry = painting.m_canvasManager.idCanvasMap.get(mapId);
                if (registry == null) continue;

                if (manipulate(itemFrame, registry, interact.getPlayer(),
                        interact.getAction() == Action.RIGHT_CLICK_AIR ||
                                interact.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                    interact.setCancelled(true);
                    break;
                }
            }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent interact) {
        if (interact.getRightClicked() instanceof ItemFrame) {
            ItemFrame itemFrame = (ItemFrame) interact.getRightClicked();
            if (itemFrame.getItem().getType() != Material.FILLED_MAP) return;
            int mapId = (int) ((MapMeta) itemFrame.getItem().getItemMeta()).getMapId();
            MapCanvasRegistry registry = painting.m_canvasManager.idCanvasMap.get(mapId);
            if (registry == null) return;

            if (manipulate(itemFrame, registry, interact.getPlayer(), true))
                interact.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean manipulate(ItemFrame itemFrameEntity, MapCanvasRegistry registry, Player player, boolean rightClicked) {
        // Calculate looking direction.
        Location itemFrame = itemFrameEntity.getLocation();
        Location playerEyePos = player.getLocation().add(0, player.getEyeHeight(), 0);
        return this.calculateUV(playerEyePos, itemFrame, (u, v) -> {
            // transform uv.
            EnumRotation rotation = EnumRotation.valueOf(itemFrameEntity.getRotation().name());
            double up = rotation.u(u, v);
            double vp = rotation.v(u, v);
            double uq = up + 0.5;
            double vq = vp + 0.5;

            int x = (int) (uq * registry.canvas.size());
            if (x >= registry.canvas.size() || x < 0) return false;

            int y = (int) (vq * registry.canvas.size());
            if (y >= registry.canvas.size() || y < 0) return false;

            // Calculate block location
            Location blockLocation = itemFrame.clone().add(-0.5 * itemFrameEntity.getFacing().getModX(),
                    0, -0.5 * itemFrameEntity.getFacing().getModZ());
            
            // Generate the interaction with the canvas
            Interaction interact = new Interaction(x, y, player, blockLocation, itemFrame, rightClicked, itemFrameEntity);

            // Paint on canvas.
            if (player.hasPermission("mpp.paint"))
                if (registry.canPaint(player)) {
                    if (painting.m_toolManager.paint(player.getItemInHand(), registry, interact)) {
                        painting.m_canvasManager.latest.put(player.getName(), registry.name);
                        return true;
                    }
                }

            if (player.hasPermission("mpp.interact"))
                if (registry.canInteract(player))
                    return registry.canvas.interact(interact);
                else return true;

            return true;
        });
    }

    public <T> T calculateUV(Location playerEyePos, Location itemFrame, BiFunction<Double, Double, T> function) {
        double yaw = (playerEyePos.getYaw() + 90) * Math.PI / 180;
        double pitch = -playerEyePos.getPitch() * Math.PI / 180;

        double a = Math.cos(yaw) * Math.cos(pitch);
        double b = Math.sin(pitch);
        double c = Math.sin(yaw) * Math.cos(pitch);

        // Calculate canvas direction.
        double dir = (itemFrame.getYaw() + 90) * Math.PI / 180;
        double A = Math.round(Math.cos(dir));
        double C = Math.round(Math.sin(dir));

        // Calculate bias vector.
        double x0 = playerEyePos.getX() - itemFrame.getX();
        double y0 = playerEyePos.getY() - itemFrame.getY();
        double z0 = playerEyePos.getZ() - itemFrame.getZ();

        // Do intersection.
        double v1 = A * x0 + C * z0;
        double v0 = A * a + C * c;
        double miu = (-v1 / v0)+ 0.008; // ED CHANGE FROM -0.04

        double xLook = x0 + miu * a;
        double yLook = y0 + miu * b;
        double zLook = z0 + miu * c;

        // Calculate uv coordination.
        double u, v = 0;
        v = yLook;

        if (Math.abs(A) > Math.abs(C)) {
            if (A > 0)
                u = -zLook;
            else u = zLook;
        } else {
            if (C > 0)
                u = xLook;
            else u = -xLook;
        }

        return function.apply(u, v);
    }
}