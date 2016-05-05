package net.aegistudio.mpp.view;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.aegistudio.mcinject.network.PacketPlayOutBlockChange;
import net.aegistudio.mcinject.network.PacketPlayOutOpenSignEditor;
import net.aegistudio.mcinject.network.PlayerConnection;
import net.aegistudio.mcinject.tileentity.TileEntitySign;
import net.aegistudio.mcinject.world.BlockPosition;
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
	
	net.aegistudio.mcinject.world.World world;
	BlockPosition blockPosition = null;
	TileEntitySign tileEntitySign = null;
	public void show() {
		try {
			Block block = findPlacableBlock(player);
			block.setType(Material.WALL_SIGN);
			block.getState().update(true, false);
			
			world = new net.aegistudio.mcinject.world.World(plugin.inject, block.getWorld());
			blockPosition = new BlockPosition(plugin.inject, block.getLocation());
			
			tileEntitySign = new TileEntitySign(plugin.inject);
			tileEntitySign.setOwner(player);
			tileEntitySign.setLine(0, title);
			tileEntitySign.setLine(1, initNaming);
			tileEntitySign.setLine(2, tip1);
			tileEntitySign.setLine(3, tip2);
			
			world.setTileEntity(blockPosition, tileEntitySign);
			PlayerConnection connection = new PlayerConnection(plugin.inject, player);
			
			connection.sendPacket(new PacketPlayOutBlockChange(plugin.inject, block.getLocation()));
			connection.sendPacket(tileEntitySign.getUpdatePacket());
			connection.sendPacket(new PacketPlayOutOpenSignEditor(plugin.inject, blockPosition));
			
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
		if(new net.aegistudio.mcinject.world.World(plugin.inject, block.getWorld()).
				getTileEntity(new BlockPosition(plugin.inject, x, y, z), null) != null) return false;
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
			world.setTileEntity(blockPosition, null);
			event.setCancelled(true);
			try { name(event.getLine(1));} catch(Throwable t) {}
			SignChangeEvent.getHandlerList().unregister(this);
		}
	}
	
	protected abstract void name(String name);
}
