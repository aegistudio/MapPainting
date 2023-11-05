/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GiveCanvasCommand
extends ActualHandle {
    public static final String ONLY_PLAYER = "onlyPlayer";
    public String onlyPlayer;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String NO_PERMISSION = "noPermission";
    public String noPermission;
    public static final String HERE_YOU_ARE = "hereYouAre";
    public String hereYouAre;
    public static final String NEED_INV = "needInv";
    public String needInv;
    public static final String CHARGED = "charged";
    public String charged;
    public static final String CANT_AFFORD = "cantAfford";
    public String cantAfford;
    public static final String BAD_QUANTITY = "badQuantity";
    public String badQuantity;

    public GiveCanvasCommand() {
        this.description = "@give.description";
        this.onlyPlayer = "@give.onlyPlayer";
        this.canvasNotExists = "@give.canvasNotExists";
        this.noPermission = "@give.noGivePermission";
        this.hereYouAre = "@give.hereYouAre $qty $canvasName";
        this.needInv = "@give.needInv";
        this.charged = "@give.charged $cost";
        this.cantAfford = "@give.cantAfford $cost";
        this.badQuantity = "@give.badQuantity";
    }

    @Override
    public boolean handle(MapPainting plugin, String prefix, CommandSender sender, String[] arguments) {
        
    	// if no arguments supplied return the complete command
    	if (arguments.length == 0) {
            sender.sendMessage(prefix + " <painting name> [quantity 1-64]");
            return true;
        }
    	
    	String clonedName = arguments[0];
    	
    	// handle quantity field
    	int quantity = 1;
    	try {
	    	if (arguments.length == 2) {
	    		quantity = Integer.parseInt(arguments[1]);
	    		if ((quantity > 64) || (quantity < 1)) {
	    			sender.sendMessage(this.badQuantity);
	    			return true;
	    		}
	    	}
    	} catch (NumberFormatException e) {
    		sender.sendMessage(this.badQuantity);
    		return true;
    	}	

    	// abort if sender not a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.onlyPlayer);
            return true;
        }        
        Player player = (Player) sender;
        
        // get canvas by name, confirm exists
        MapCanvasRegistry canvas = plugin.getCanvas(clonedName, sender);
        if (canvas == null) {
            sender.sendMessage(this.canvasNotExists.replace("$canvasName", clonedName));
            return true;
        }
        
        // check player has sufficient rights to give the canvas
        if (!canvas.hasPermission(sender, "give")) {
            sender.sendMessage(this.noPermission.replace("$canvasName", clonedName));
            return true;
        }
        
        // check if player has enough free slots to receive the painting
        if (player.getInventory().firstEmpty() == -1){
        	sender.sendMessage(this.needInv);
        	return true;
        }
        
        // check player can afford if economy is active
    	Economy econ = plugin.getEconomy();    	
    	int cost = 0;
    	double balance = 0;

    	// check balance
    	if (econ != null){
    		
    		cost = plugin.costClone * quantity;
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
        
        // actually give the item
        plugin.m_canvasManager.give((Player)sender, canvas, quantity);
        
        // feedback
        String result = this.hereYouAre.replace("$canvasName", clonedName);
        result = result.replace("$qty", String.valueOf(quantity));
        sender.sendMessage(result);
        
        return true;
    }

    @Override
    public void load(MapPainting plugin, ConfigurationSection section) throws Exception {
        super.load(plugin, section);
        this.canvasNotExists = plugin.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
        this.noPermission = plugin.getLocale(NO_PERMISSION, this.noPermission, section);
        this.onlyPlayer = plugin.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
        this.hereYouAre = plugin.getLocale(HERE_YOU_ARE, this.hereYouAre, section);
        this.needInv = plugin.getLocale(NEED_INV, this.needInv,section);
        this.charged = plugin.getLocale(CHARGED, this.charged,section);
        this.cantAfford = plugin.getLocale(CANT_AFFORD, this.cantAfford,section);
        this.badQuantity = plugin.getLocale(BAD_QUANTITY, this.badQuantity,section);
    }
}

