package net.aegistudio.mpp.factory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

/**
 * Use template method to construct create sub-commands.
 * @author aegistudio
 */

public abstract class ConcreteCreateSubCommand extends ActualHandle {
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
			ItemStack item = player.getItemInHand();
			if(item.getType() != Material.MAP) {
				sender.sendMessage(painting.create.shouldHoldMap);
				return true;
			}
			
			short map = item.getDurability();
			if(painting.canvas.idCanvasMap.containsKey(map)) {
				sender.sendMessage(painting.create.mapAlreadyBound);
				return true;
			}
			
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
				canvas.binding = map;
				canvas.canvas = canvasInstance;
				
				canvas.owner = player.getName();
				canvas.painter.add(player.getName());
				canvas.view = painting.getServer().getMap(map);
				
				canvas.add();
				
				painting.canvas.nameCanvasMap.put(canvas.name, canvas);
				painting.canvas.idCanvasMap.put(canvas.binding, canvas);
				sender.sendMessage(painting.create.bound.replace("$canvasName", name));
			}
		}
		return true;
	}
	
	protected abstract Canvas create(MapPainting painting, CommandSender sender, String[] arguments);
}
