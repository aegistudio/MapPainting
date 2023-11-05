/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 */
package net.aegistudio.mpp.factory;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class CloneSubCommand
extends ConcreteCreateSubCommand {
    public static final String CLONED_UNSPECIFIED = "clonedUnspecified";
    public String clonedUnspecified;
    public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
    public String canvasNotExists;
    public static final String NEED_INV = "needInv";
    public String needInv;
    public static final String CANT_AFFORD = "cantAfford";
    public String cantAfford;
    public static final String CHARGED = "charged";
    public String charged;
    public static final String NO_PERMISSION = "noPermission";
    public String noPermission;
    

    public CloneSubCommand() {
        this.description = "@create.clone.description";
        this.paramList = "<cloned>";
        this.clonedUnspecified = "@create.clone.clonedUnspecified";
        this.canvasNotExists = "@create.clone.canvasNotExists";
        this.needInv = "@create.clone.needInv";
        this.cantAfford = "@create.clone.cantAfford $cost";
        this.charged = "@create.clone.charged $cost";
        this.noPermission = "@create.clone.noPermission $canvasName";
    }

    @Override
    protected Canvas create(MapPainting plugin, CommandSender sender, String[] arguments) {
        
    	// check sender is a player
    	if (!(sender instanceof Player)) {
    		sender.sendMessage(plugin.m_commandCreateHandler.onlyPlayer);
    		return null;
    	}
    	
    	Player player = (Player) sender;
    	
        // check if player has enough free slots to receive the painting
        if (player.getInventory().firstEmpty() == -1){
        	sender.sendMessage(this.needInv);
        	return null;
        }
    	
    	// check player can afford if economy is active
    	Economy econ = plugin.getEconomy();
    	int cost = 0;
    	double balance = 0;

    	// check if player can afford
    	if (econ != null){
    		cost = plugin.costCopy;
    		balance = econ.getBalance(player);
    		
    		if (balance < cost) {
    			sender.sendMessage(this.cantAfford.replace("$cost", String.valueOf(cost)));
    			return null;
    		}
    		
    	}
    	
    	// check sender has sufficient perms
    	if (!sender.hasPermission("mpp.create.clone")) {
            sender.sendMessage(plugin.m_commandCreateHandler.noCreatePermission);
            return null;
        }
    	
    	if (arguments.length == 0) {
            sender.sendMessage(this.clonedUnspecified);
            return null;
        }
        
    	String name = arguments[0];
        MapCanvasRegistry entry = plugin.getCanvas(name, sender);
        
        
        if (entry == null) {
            sender.sendMessage(this.canvasNotExists.replace("$canvasName", name));
            return null;
        }
        
     // check player has sufficient rights to copy the canvas
        if (!entry.hasPermission(sender, "give")) {
            sender.sendMessage(this.noPermission.replace("$canvasName", name));
            return null;
        }
        
        // ECON COST OF CREATING THE CANVAS
        if (econ != null){
        	EconomyResponse transaction = econ.withdrawPlayer(player,  cost);
        	if (transaction.transactionSuccess()) {
        		sender.sendMessage(this.charged.replace("$cost", String.valueOf(cost)));
        	} else {
        		sender.sendMessage(this.cantAfford.replace("$cost", String.valueOf(cost)));
        	}
    	}
        
        return entry.canvas.clone();
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        this.clonedUnspecified = painting.getLocale(CLONED_UNSPECIFIED, this.clonedUnspecified, section);
        this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
    }
}

