package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class PigmentCommand extends ActualHandle {
	{ description = "Give the player a pigment of specified color."; }
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = ChatColor.RED + "Only player can use pigment command.";
	
	public static final String NO_PIGMENT_PERMISSION = "noPermission";
	public String noPigmentPermission = ChatColor.RED + "You don't have permission to issue pigment command.";
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = ChatColor.RED + "The color you input is not right! Please enter a integer!";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		noPigmentPermission = painting.getLocale(NO_PIGMENT_PERMISSION, noPigmentPermission, section);
		invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
	}
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 3) {
			sender.sendMessage(prefix + " <red> <green> <blue>");
			return true;
		}
		else {
			if(!(sender instanceof Player)) {
				sender.sendMessage(onlyPlayer);
				return true;
			}
			
			if(!sender.hasPermission("mpp.pigment")) {
				sender.sendMessage(noPigmentPermission);
				return true;
			}
			int red, green, blue;
			try {
				red = Integer.parseInt(arguments[0]);
				green = Integer.parseInt(arguments[1]);
				blue = Integer.parseInt(arguments[2]);
			}
			catch(Throwable t) {
				sender.sendMessage(invalidFormat);
				return true;		
			}
			
			Player player = (Player) sender;
			ItemStack item = new ItemStack(Material.INK_SACK);
			painting.palette.dye.setColor(item, new Color(red, green, blue));
			player.getInventory().addItem(item);
			
			return true;
		}
	}
}
