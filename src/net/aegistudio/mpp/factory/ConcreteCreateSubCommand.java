package net.aegistudio.mpp.factory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

/**
 * Use template method to construct create sub-commands.
 * @author aegistudio
 */

public abstract class ConcreteCreateSubCommand extends ActualHandle implements HazardCommand {
	protected String paramList = "[<parameter>]";
	
	@SuppressWarnings("deprecation")
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length < 1) 
			sender.sendMessage(prefix + " <name> " + paramList);
		else {
			if(!(sender instanceof Player)) {
				sender.sendMessage(painting.create.onlyPlayer);
				return true;
			}
			
			Player player = (Player) sender;
			
			String name = arguments[0];
			if(painting.canvas.nameCanvasMap.containsKey(name)) {
				sender.sendMessage(painting.create.canvasAlreadyExisted.replace("$canvasName", name));
				return true;
			}
			
			String[] subArguments = new String[arguments.length - 1];
			System.arraycopy(arguments, 1, subArguments, 0, arguments.length - 1);
			Canvas canvasInstance = this.create(painting, player, subArguments);
			
			if(canvasInstance != null) {
				MapCanvasRegistry canvas = new MapCanvasRegistry(name);
				canvas.canvas = canvasInstance;
				
				canvas.owner = player.getName();
				canvas.painter.add(player.getName());
				
				ItemStack item = player.getItemInHand();
				if(item.getType() != Material.MAP) {
					sender.sendMessage(painting.create.notHoldingMap);
					painting.hazard.hazard(sender, this, canvas);
					return true;
				}
				else {
					short map = item.getDurability();
					if(painting.canvas.idCanvasMap.containsKey(map)) {
						sender.sendMessage(painting.create.mapAlreadyBound);
						painting.hazard.hazard(sender, this, canvas);
						return true;
					}
					
					canvas.binding = map;
					canvas.view = painting.getServer().getMap(map);
					
					this.commit(painting, sender, canvas);
				}
			}
		}
		return true;
	}
	
	protected void commit(MapPainting painting, CommandSender sender, MapCanvasRegistry canvas) {
		painting.canvas.add(canvas);
		
		sender.sendMessage(painting.create.bound.replace("$canvasName", canvas.name));
		painting.ackHistory(canvas, sender);
	}
	
	protected abstract Canvas create(MapPainting painting, CommandSender sender, String[] arguments);
	
	@SuppressWarnings("deprecation")
	public void handle(MapPainting painting, CommandSender sender, Object state) {
		MapCanvasRegistry registry = (MapCanvasRegistry) state;
		MapView view = painting.getServer().createMap(painting.getServer().getWorlds().get(0));
		if(view == null) {
			sender.sendMessage(painting.create.tooManyMap);
			return;
		}
		
		registry.binding = view.getId();
		registry.view = view;

		if(sender instanceof Player) {
			Player player = (Player) sender;
			player.getInventory().addItem(new ItemStack(Material.MAP, 1, registry.binding));
		}
		
		this.commit(painting, sender, registry);
	}
}
