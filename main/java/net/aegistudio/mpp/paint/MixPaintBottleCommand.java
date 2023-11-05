/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 * org.bukkit.Material
 * org.bukkit.command.CommandSender
 * org.bukkit.configuration.ConfigurationSection
 * org.bukkit.entity.Player
 * org.bukkit.inventory.ItemStack
 * org.bukkit.inventory.PlayerInventory
 */
package net.aegistudio.mpp.paint;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.color.PseudoColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.awt.*;

public class MixPaintBottleCommand extends ActualHandle {
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer;
	public static final String NO_PIGMENT_PERMISSION = "noPermission";
	public String noPigmentPermission;
	public static final String INVALID_FORMAT = "invalidFormat";
	public String invalidFormat;
	public static final String CHARGED = "charged";
	public String charged;
	public static final String CANT_AFFORD = "cantAfford";
	public String cantAfford;
	public static final String NEED_INV = "needInv";
	public String needInv;
	public static final String GAVE_PIGMENT = "gavePigment $name";
	public String gavePigment;

	public MixPaintBottleCommand() {
		this.description = "@pigment.description";
		this.onlyPlayer = "@pigment.onlyPlayer";
		this.noPigmentPermission = "@pigment.noPigmentPermission";
		this.invalidFormat = "@pigment.invalidFormat";
		this.charged = "@pigment.charged $cost";
		this.cantAfford = "@pigment.charged $cost";
		this.needInv = "@pigment.needInv";
		this.gavePigment = "@pigment.gavePigment $name";
	}

	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
		this.noPigmentPermission = painting.getLocale(NO_PIGMENT_PERMISSION, this.noPigmentPermission, section);
		this.invalidFormat = painting.getLocale(INVALID_FORMAT, this.invalidFormat, section);
		this.charged = painting.getLocale(CHARGED, this.charged, section);
	}

	@Override
	public boolean handle(MapPainting plugin, String prefix, CommandSender sender, String[] arguments) {
		
		Boolean basic = true;
		
		// make sure this is an actual player sending the command, reject otherwise
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.onlyPlayer);
			return true;
		}
		
		// make sure sender has pigment perms
		if (!sender.hasPermission("mpp.pigment")) {
			sender.sendMessage(noPigmentPermission);
			return true;
		}
		
		// make sure the command has arguments
		if (arguments.length == 0) {
			sender.sendMessage(invalidFormat);
			return true;
		}
		
		PseudoColor Pcolor = null;
		
		// remap legacy commands to be handled (MLMC)
		if (arguments[0].toLowerCase() == "rgb") {
			arguments[0] = arguments[1];
			arguments[1] = arguments[2];
			arguments[2] = arguments[3];
		}
		
		// check if full RGB value provided
		if (arguments.length >= 3) {
			
			basic = false;
			
			// R VALUE DEFINITION
			
			int pigment_r = 0;
			try { pigment_r = Integer.parseInt(arguments[0]);}
			catch (NumberFormatException e)
			{
			   sender.sendMessage(invalidFormat);
			   return true;
			}
				
			if ((pigment_r > 255) || (pigment_r < 0)){ 
				sender.sendMessage(invalidFormat);
				return true;
			}
			
			// G VALUE DEFINITION
			
			int pigment_g = 0;
			try { pigment_g = Integer.parseInt(arguments[1]);}
			catch (NumberFormatException e)
			{
			   sender.sendMessage(invalidFormat);
			   return true;
			}
				
			if ((pigment_g > 255) || (pigment_g < 0)){ 
				sender.sendMessage(invalidFormat);
				return true;
			}
			
			// B VALUE DEFINITION
			
			int pigment_b = 0;
			try { pigment_b = Integer.parseInt(arguments[2]);}
			catch (NumberFormatException e)
			{
			   sender.sendMessage(invalidFormat);
			   return true;
			}
				
			if ((pigment_b > 255) || (pigment_b < 0)){ 
				sender.sendMessage(invalidFormat);
				return true;
			}
			
			// Update the color being created
			Pcolor = new PseudoColor(pigment_r, pigment_g, pigment_b);
			
		}
		
		// check default color maps
		if (Pcolor == null){
			try {
				Pcolor = plugin.m_colorManager.parseColor(arguments[0]);
				if(Pcolor == null) throw new RuntimeException();
			}
				catch(RuntimeException e) {
					sender.sendMessage(invalidFormat);
					return true;
			}
		}
		
		Player player = (Player) sender;
		
		// check if player has enough free slots to receive the pigment
        if (player.getInventory().firstEmpty() == -1){
        	sender.sendMessage(this.needInv);
        	return true;
        }
        
        // check player can afford if economy is active
    	Economy econ = plugin.getEconomy();    	
    	int cost = 0;
    	double balance = 0;
    	
    	if (basic){
			cost = plugin.costPaintBasic;
		} else {
			cost = plugin.costPaintRGB;
		}

    	// check balance
    	if (econ != null){	
    		balance = econ.getBalance(player);
    		if (balance < cost) {
    			sender.sendMessage(this.cantAfford.replace("$cost", String.valueOf(cost)));
    			return true;
    		}
    	}
    	
    	// charge the player appropriately
        if (econ != null){
        	EconomyResponse transaction = econ.withdrawPlayer(player,  cost);
        	if (transaction.transactionSuccess()) {
        		sender.sendMessage(this.charged.replace("$cost", String.valueOf(cost)));
        	} else {
        		sender.sendMessage(this.cantAfford.replace("$cost", String.valueOf(cost)));
        		return true;
        	}
    	}

        // Actually generate the item and give it to the player
		ItemStack item = plugin.m_paintManager.getPaintBottle(Pcolor.color);
		player.getInventory().addItem(item);
		
		sender.sendMessage(this.gavePigment);
		
		return true;
	}
}
