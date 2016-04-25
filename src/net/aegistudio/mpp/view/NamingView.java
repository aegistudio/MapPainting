package net.aegistudio.mpp.view;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.aegistudio.mpp.MapPainting;

/**
 * This is an inventory view intended for naming or
 * renaming. It could be used both by dummy canvas
 * or an normal canvas.
 * 
 * @author aegistudio
 */

public abstract class NamingView implements Listener {
	MapPainting plugin;
	String title, initNaming, tip1, tip2;
	Player player;
	public NamingView(MapPainting plugin, String title, 
			String initNaming, String tip1, String tip2, Player player) {
		this.plugin = plugin;
		this.title = title;
		this.initNaming = initNaming;
		this.tip1 = tip1;
		this.tip2 = tip2;
		this.player = player;
	}
	
	World world;
	Object blockPosition = null;
	Object tileEntitySign = null;
	public void show() {
		try {
			Block block = findPlacableBlock(player);
			block.setType(Material.WALL_SIGN);
			block.getState().update(true, false);
			
			world = player.getWorld();
			blockPosition = plugin.posign.newBlockLocation(block.getX(), block.getY(), block.getZ());
			tileEntitySign = plugin.posign.newTileEntitySign(plugin.sender.getHandle(player));
			plugin.posign.setText(tileEntitySign, 0, title);
			plugin.posign.setText(tileEntitySign, 1, initNaming);
			plugin.posign.setText(tileEntitySign, 2, tip1);
			plugin.posign.setText(tileEntitySign, 3, tip2);
			
			plugin.world.setTileEntity(world, blockPosition, tileEntitySign);
			
			plugin.sender.sendPacket(player, 
					plugin.world.updateBlock(world, blockPosition));
			plugin.sender.sendPacket(player, 
					plugin.posign.newSignUpdatePacket(tileEntitySign));
			plugin.sender.sendPacket(player, 
					plugin.posign.newSignEditPacket(blockPosition));
			
			this.plugin.getServer().getPluginManager()
				.registerEvents(this, plugin);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Block findPlacableBlock(Player player) throws Exception {
		Location playerLocation = player.getLocation();
		World world = playerLocation.getWorld();
		int x = playerLocation.getBlockX();
		int z = playerLocation.getBlockZ();
	
		int i = 0;
		while(i < 16384) {
			Block block = world.getBlockAt(
					x + (int)(i * Math.random()), 254,
					z + (int)(i * Math.random()));
			if(suitable(block)) return block;
			i ++;
		}
		throw new Exception("There may be no air in your current world.");
	}
	
	public boolean suitable(Block block) throws Exception {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		if(plugin.world.getTileEntity(block.getWorld(), 
				plugin.posign.newBlockLocation(x, y, z)) != null) return false;
		if(block.getType() != Material.AIR) return false;
		
		if(block.getWorld().getBlockAt(x, y + 1, z)
				.getType() == Material.WALL_SIGN) return false;
		if(block.getWorld().getBlockAt(x, y - 1, z)
				.getType() == Material.WALL_SIGN) return false;
		if(block.getWorld().getBlockAt(x + 1, y, z)
				.getType() == Material.WALL_SIGN) return false;
		if(block.getWorld().getBlockAt(x - 1, y, z)
				.getType() == Material.WALL_SIGN) return false;
		if(block.getWorld().getBlockAt(x, y, z + 1)
				.getType() == Material.WALL_SIGN) return false;
		if(block.getWorld().getBlockAt(x, y, z - 1)
				.getType() == Material.WALL_SIGN) return false;
		return true;
	}
	
	@EventHandler
	public void onSignEdit(SignChangeEvent event) {
		if(event.getPlayer() == player) {
			event.getBlock().setType(Material.AIR);
			plugin.world.setTileEntity(world, blockPosition, null);
			event.setCancelled(true);
			try { name(event.getLine(1));} catch(Throwable t) {}
			SignChangeEvent.getHandlerList().unregister(this);
		}
	}
	
	protected abstract void name(String name);
}
