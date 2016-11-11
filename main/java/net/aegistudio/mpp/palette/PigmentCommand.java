package net.aegistudio.mpp.palette;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.color.PseudoColor;

public class PigmentCommand extends ActualHandle {
	{ description = "@pigment.description"; }
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer = "@pigment.onlyPlayer";
	
	public static final String NO_PIGMENT_PERMISSION = "noPermission";
	public String noPigmentPermission = "@pigment.noPigmentPermission";
	
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat = "@pigment.invalidFormat";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		super.load(painting, section);
		onlyPlayer = painting.getLocale(ONLY_PLAYER, onlyPlayer, section);
		noPigmentPermission = painting.getLocale(NO_PIGMENT_PERMISSION, noPigmentPermission, section);
		invalidFormat = painting.getLocale(INVALID_FORMAT, invalidFormat, section);
	}
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length != 1) {
			sender.sendMessage(prefix + " <color>");
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
			
			PseudoColor color = null;
			try {
				color = painting.color.parseColor(arguments[0]);
				if(color == null) throw new RuntimeException();
			}
			catch(RuntimeException e) {
				sender.sendMessage(invalidFormat);
				return true;
			}
			
			Player player = (Player) sender;
			ItemStack item = null;
			
			if(color.color != null) {
				item = new ItemStack(Material.INK_SACK);
				painting.palette.dye.setColor(item, color.color);
			}
			else item = new ItemStack(Material.SHEARS);
			player.getInventory().addItem(item);
			
			return true;
		}
	}
}
