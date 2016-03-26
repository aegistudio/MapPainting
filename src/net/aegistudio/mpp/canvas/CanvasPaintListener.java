package net.aegistudio.mpp.canvas;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;

public class CanvasPaintListener implements Listener {
	private final MapPainting painting;
	public CanvasPaintListener(MapPainting painting) {
		this.painting = painting;
	}
	
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent attack) {
		if(!(attack.getEntity() instanceof ItemFrame)) return;
		if(!(attack.getDamager() instanceof Player)) return;
		
		ItemFrame itemFrame = (ItemFrame) attack.getEntity();
		if(itemFrame.getItem().getType() != Material.MAP) return;
		short mapId = itemFrame.getItem().getDurability();
		
		MapCanvasRegistry registry = painting.canvas.idCanvasMap.get(mapId);
		if(registry == null) return;
		
		Player player = (Player) attack.getDamager();
		if(manipulate(itemFrame, registry, player, false))
			attack.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent interact) {
		if(interact.getClickedBlock() == null) return;
		BlockFace blockFace = interact.getBlockFace();
		if(blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) return;
		
		Collection<Entity> entities = interact.getClickedBlock().getWorld()
			.getNearbyEntities(interact.getClickedBlock()
					.getLocation().add(0.5, 0.5, 0.5)
						.add(blockFace.getModX(), 0, blockFace.getModZ()), 0.5, 0.5, 0.5);
		for(Entity entity : entities) 
				if(entity instanceof ItemFrame){
			ItemFrame itemFrame = (ItemFrame) entity;
			if(itemFrame.getItem().getType() != Material.MAP) return;
			short mapId = itemFrame.getItem().getDurability();
			MapCanvasRegistry registry = painting.canvas.idCanvasMap.get(mapId);
			if(registry == null) break;
			
			if(manipulate(itemFrame, registry, interact.getPlayer(), 
					interact.getAction() == Action.RIGHT_CLICK_AIR || interact.getAction() == Action.RIGHT_CLICK_BLOCK)) 
				interact.setCancelled(true);
			break;
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent interact) {
		if(interact.getRightClicked() instanceof ItemFrame) {
			ItemFrame itemFrame = (ItemFrame) interact.getRightClicked();
			if(itemFrame.getItem().getType() != Material.MAP) return;
			short mapId = itemFrame.getItem().getDurability();
			MapCanvasRegistry registry = painting.canvas.idCanvasMap.get(mapId);
			if(registry == null) return;
			
			if(manipulate(itemFrame, registry, interact.getPlayer(), true))
				interact.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean manipulate(ItemFrame itemFrameEntity, MapCanvasRegistry registry, Player player, boolean rightClicked) {
		// Calculate looking direction.
		Location itemFrame = itemFrameEntity.getLocation();
		Location playerEyePos = player.getLocation().add(0, player.getEyeHeight(), 0);
		
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
		double miu = (-v1 / v0) - 0.04;
		
		double xLook = x0 + miu * a;
		double yLook = y0 + miu * b;
		double zLook = z0 + miu * c;
		
		// Calculate uv coordination.
		double u, v = 0;	v = yLook;
		
		if(Math.abs(A) > Math.abs(C)) {
			if(A > 0) 
				u = -zLook;
			else u = zLook;
		}
		else {
			if(C > 0)
				u = xLook;
			else u = -xLook;
		}
		
		// transform uv.
		EnumRotation rotation = EnumRotation.valueOf(itemFrameEntity.getRotation().name());
		double up = rotation.u(u, v);
		double vp = rotation.v(u, v);
		u = up + 0.5; v = vp + 0.5;
		
		int x = (int)(u * registry.canvas.size());
		int y = (int)(v * registry.canvas.size());
		
		// Calculate block location
		Location blockLocation = itemFrame.clone().add(-A / 2, 0, -C / 2);
		
		Interaction interact = new Interaction(x, y, player, blockLocation, itemFrame, rightClicked);
		
		// Paint on canvas.
		if(player.hasPermission("mpp.paint"))
			if(registry.painter.contains(player.getName()))
				if(painting.tool.paint(player.getItemInHand(), registry, interact)) {
					painting.canvas.latest.put(player.getName(), registry.name);
					return true;
				}
		if(player.hasPermission("mpp.interact"))
			return registry.canvas.interact(interact);
		return false;
	}
}
