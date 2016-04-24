package net.aegistudio.mpp.canvas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.Module;

public class CanvasScopeListener implements Module, Listener {
	public MapPainting plugin;
	
	public static final String IDENTIFIER = "identifier";
	public String identifier = ChatColor.RESET + ChatColor.BOLD.toString() + "Canvas Item";
	
	public static final String ID_STRING = "mapIdString";
	public String mapIdString = ChatColor.MAGIC + "===========";
	
	public CanvasScopeListener(MapPainting painting) {
		this.plugin = painting;
	}

	public void make(ItemStack item, MapCanvasRegistry registry) {
		item.setType(Material.PAINTING);
		item.setAmount(1);
		item.setDurability((short)0);
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(registry.name);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(identifier);
		lore.add(mapIdString + registry.binding);
		meta.setLore(lore);
		
		meta.addEnchant(Enchantment.DURABILITY, 0, false);
		
		item.setItemMeta(meta);
	}
	
	public int parse(ItemStack item) {
		if(item == null) return -1;
		if(item.getType() != Material.PAINTING) return -1;
		List<String> lores = item.getItemMeta().getLore();
		if(lores == null) return -1;
		if(lores.size() < 2) return -1;
		if(!lores.get(0).equals(identifier)) return -1;
		return Integer.parseInt(lores.get(1).substring(mapIdString.length()));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {		
		int result = parse(event.getItem());
		if(result < 0) return;
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if(block == null) return;
			try {
				MapCanvasRegistry registry = plugin.canvas.idCanvasMap.get((short)result);
				if(registry != null && !registry.removed()) 
					placeFrame(block.getLocation(), event.getBlockFace(), registry);
				
				// Consume map item.
				if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
					int newAmount = event.getPlayer().getItemInHand().getAmount() - 1;
					if(newAmount > 0) event.getPlayer().getItemInHand().setAmount(newAmount);
					else event.getPlayer().setItemInHand(null);
				}
			}
			catch(Throwable e) {
				
			}
			event.setCancelled(true);
		}
	}
	
	public void placeFrame(Location blockLocation, BlockFace blockFace, MapCanvasRegistry registry) {
		// Place map item frame.
		ItemFrame frame = blockLocation.getWorld().spawn(blockLocation.add(
				blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()), ItemFrame.class);
		frame.setFacingDirection(blockFace);
		
		// Set some meta for the item.
		ItemStack mapitem = new ItemStack(Material.MAP, 1, registry.binding);
		//ItemMeta mapitemMeta = mapitem.getItemMeta();
		//mapitemMeta.setDisplayName(registry.name);
		//mapitem.setItemMeta(mapitemMeta);
		frame.setItem(mapitem);
		
		registry.canvas.place(blockLocation, blockFace);
	}
	
	@EventHandler
	public void onSpawnItem(ItemSpawnEvent e) {
		ItemStack item = e.getEntity().getItemStack();
		if(item.getType() != Material.MAP) return;
		short mapid = item.getDurability();
		MapCanvasRegistry registry = plugin.canvas.idCanvasMap.get(mapid);
		if(registry != null && !registry.removed()) {
			this.make(item, registry);
			e.getEntity().setItemStack(item);
			removeNearby(e.getEntity().getLocation(), mapid);
			registry.canvas.unplace(e.getEntity());
		}
		else if(plugin.canvas.pool.contains(mapid)) {
			e.getEntity().remove();
			removeNearby(e.getEntity().getLocation(), mapid);
		}
	}
	
	public void removeNearby(Location location, short mapid) {
		location.getWorld().getNearbyEntities(location, .5, .5, .5)
			.forEach((entity) -> {
				if(entity.getType() == EntityType.DROPPED_ITEM) {
					if(((Item) entity).getItemStack().getType() == Material.ITEM_FRAME)
						entity.remove();
				}
				else if(entity.getType() == EntityType.ITEM_FRAME) {
					ItemStack mapitem = ((ItemFrame) entity).getItem();
					if(mapitem.getType() == Material.MAP)
						if(mapitem.getDurability() == mapid)
							entity.remove();
				}
			});
	}

	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		identifier = painting.getLocale(IDENTIFIER, identifier, section);
		mapIdString = painting.getLocale(ID_STRING, mapIdString, section);
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {
		section.set(IDENTIFIER, identifier);
		section.set(ID_STRING, mapIdString);
	}
}
