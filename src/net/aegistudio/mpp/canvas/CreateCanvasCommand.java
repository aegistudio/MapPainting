package net.aegistudio.mpp.canvas;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class CreateCanvasCommand extends ActualHandle {
	{ description = "Create a new canvas based on arguments."; }
	
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use create canvas command.";
	
	public static final String NO_CREATE_PERMISSION = "noCreatePermission";
	public String noCreatePermission = ChatColor.RED + "You don't have permission to create canvas of type " 
			+ ChatColor.GREEN + "$typeName" + ChatColor.RED + "!";
	
	public static final String SHOULD_HOLD_MAP = "shouldHoldMap";
	public String shouldHoldMap = ChatColor.RED + "You should hold a map in your hand to turn it into canvas.";
	
	public static final String MAP_ALREADY_BOUND = "mapAlreadyBound";
	public String mapAlreadyBound = ChatColor.RED + "The map you're holding has already been bound to a map.";
	
	public static final String CANVAS_ALREADY_EXISTED = "canvasAlreadyExisted";
	public String canvasAlreadyExisted = ChatColor.RED + "The canvas " + ChatColor.AQUA + "$canvasName" + ChatColor.RED + " has already existed!";
	
	public static final String TYPE_UNSUPPORTED = "typeUnsupported";
	public String typeUnsupported = ChatColor.RED + "The type " + ChatColor.GREEN + "$typeName" + ChatColor.RED + " is not supported on this server!";
	
	public static final String BOUND = "bound";
	public String bound = "You have successfully bound " + ChatColor.AQUA + "$canvasName" + ChatColor.RESET + "!";
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(sender instanceof Player) { 
			Player player = (Player)sender;
			if(arguments.length < 2) {
				sender.sendMessage(prefix + " <type> <name> [<parameter>]");
				return true;
			}
			else {
				ItemStack item = player.getItemInHand();
				if(item.getType() != Material.MAP) {
					sender.sendMessage(shouldHoldMap);
					return true;
				}
				
				short map = item.getDurability();
				if(painting.canvas.idCanvasMap.containsKey(map)) {
					sender.sendMessage(mapAlreadyBound);
					return true;
				}
				
				String name = arguments[1];
				if(painting.canvas.nameCanvasMap.containsKey(name)) {
					sender.sendMessage(canvasAlreadyExisted.replace("$canvasName", name));
					return true;
				}
				
				CanvasFactory factory = EnumCanvasFactory.getFactory(arguments[0]);
				if(factory == null) {
					sender.sendMessage(typeUnsupported.replace("$typeName", arguments[0]));
					return true;
				}
				
				MapCanvasRegistry canvas = new MapCanvasRegistry(name);
				canvas.binding = map;
				
				String[] subArguments = new String[arguments.length - 2];
				System.arraycopy(arguments, 2, subArguments, 0, arguments.length - 2);
				canvas.canvas = factory.create(player, subArguments);
				if(canvas.canvas == null) {
					sender.sendMessage(noCreatePermission.replace("$typeName", arguments[0]));
					return true;
				}
				
				canvas.owner = player.getName();
				canvas.painter.add(player.getName());
				canvas.view = painting.getServer().getMap(map);
				
				canvas.add();
				
				painting.canvas.nameCanvasMap.put(canvas.name, canvas);
				painting.canvas.idCanvasMap.put(canvas.binding, canvas);
				sender.sendMessage(bound.replace("$canvasName", name));
			}
		}
		else 
			sender.sendMessage(onlyPlayer);
		
		return true;
	}
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		onlyPlayer = this.getLocale(ONLY_PLAYER, onlyPlayer, section);
		noCreatePermission = this.getLocale(NO_CREATE_PERMISSION, noCreatePermission, section);
		shouldHoldMap = this.getLocale(SHOULD_HOLD_MAP, shouldHoldMap, section);
		mapAlreadyBound = this.getLocale(MAP_ALREADY_BOUND, mapAlreadyBound, section);
		canvasAlreadyExisted = this.getLocale(CANVAS_ALREADY_EXISTED, canvasAlreadyExisted, section);
		typeUnsupported = this.getLocale(TYPE_UNSUPPORTED, typeUnsupported, section);
		bound = this.getLocale(BOUND, bound, section);
	}
}
