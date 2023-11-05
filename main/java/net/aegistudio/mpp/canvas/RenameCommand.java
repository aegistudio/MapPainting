/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 * org.bukkit.command.CommandSender
 * org.bukkit.configuration.ConfigurationSection
 * org.bukkit.entity.Player
 * org.bukkit.inventory.ItemStack
 */
package net.aegistudio.mpp.canvas;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RenameCommand extends ActualHandle {
	public static final String ONLY_PLAYER = "onlyPlayer";
	public String onlyPlayer;
	public static final String CANVAS_NOT_EXISTS = "canvasNotExists";
	public String canvasNotExists;
	public static final String CANVAS_ALREADY_EXIST = "canvasAlreadyExist";
	public String canvasAlreadyExist;
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding;
	public static final String NO_RENAME_PERMISSION = "noRenamePermission";
	public String noRenamePermission;
	public static final String SUCCESSFULLY_RENAME = "successfullyRename";
	public String successfullyRename;
	public static final String CANT_AFFORD = "cantAfford";
	public String cantAfford;
	public static final String CHARGED = "charged";
	public String charged;

	public RenameCommand() {
		this.description = "@rename.description";
		this.onlyPlayer = "@rename.onlyPlayer";
		this.canvasNotExists = "@rename.canvasNotExists";
		this.canvasAlreadyExist = "@rename.canvasAlreadyExist";
		this.notHolding = "@rename.notHolding";
		this.noRenamePermission = "@rename.noRenamePermission";
		this.successfullyRename = "@rename.successfullyRename";
		this.cantAfford = "@rename.cantAfford $cost";
		this.charged = "@rename.charged $cost";
	}

	@Override
	public boolean handle(MapPainting plugin, String prefix, CommandSender sender, String[] arguments) {
		
		// if no arguments are supplied, advise player the right command
		if (arguments.length == 0) {
			sender.sendMessage(prefix + " [<oldname>] <newname>");
			return true;
		}
		
		MapCanvasRegistry oldcanvas;
		String newname;
			
		// only rename by players
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.onlyPlayer);
			return true;
		}
		Player player = (Player) sender;
		
		// if only one name is supplied, assume renaming the held canvas
		if (arguments.length == 1) {
				
				oldcanvas = plugin.m_canvasManager.holding(player);
				if (oldcanvas == null) {
					sender.sendMessage(this.notHolding);
					return true;
				}
				newname = arguments[0];
			
		}
		
		else {
				
			// handled when both oldname and newname are supplied
				oldcanvas = plugin.m_canvasManager.nameCanvasMap.get(arguments[0]);
				if (null == oldcanvas || oldcanvas.removed()) {
					sender.sendMessage(this.canvasNotExists.replace("$canvasName", arguments[0]));
					return true;
				}
				newname = arguments[1];
		}
		
		// sanitise newname
		newname = newname.replace('.', '_');
		newname = newname.replace('/', '_');
		newname = newname.replace('\\', '_');
		
		// confirm sender has rename permissions	
		if (!oldcanvas.hasPermission(sender, "rename")) {
			sender.sendMessage(this.noRenamePermission);
			return true;
		}
		
		// confirm we arent duplicating a painting name
		if (plugin.m_canvasManager.nameCanvasMap.containsKey(newname)) {
			sender.sendMessage(this.canvasAlreadyExist.replace("$canvasName", newname));
			return true;
		}
        
        // check player can afford if economy is active
    	Economy econ = plugin.getEconomy();    	
    	int cost = 0;
    	double balance = 0;

    	if (econ != null){
    		
    		cost = plugin.costRename;
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
		
		String oldname = oldcanvas.name;
		plugin.m_canvasManager.nameCanvasMap.put(newname, oldcanvas);
		oldcanvas.name = newname;
		plugin.m_canvasManager.nameCanvasMap.remove(oldname);
		
		sender.sendMessage(this.successfullyRename.replace("$oldname", oldname).replace("$newname", newname));
		
		// actually replace the item in hand with renamed item
		// TODO - does this handle stacks properly? inv space issues?
		if (sender instanceof Player) {
			plugin.m_canvasManager.scopeListener.make(player.getInventory().getItemInMainHand(), oldcanvas);
		}
		
		return true;
	}

	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.onlyPlayer = painting.getLocale(ONLY_PLAYER, this.onlyPlayer, section);
		this.canvasNotExists = painting.getLocale(CANVAS_NOT_EXISTS, this.canvasNotExists, section);
		this.canvasAlreadyExist = painting.getLocale(CANVAS_ALREADY_EXIST, this.canvasAlreadyExist, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, this.notHolding, section);
		this.noRenamePermission = painting.getLocale(NO_RENAME_PERMISSION, this.noRenamePermission, section);
		this.successfullyRename = painting.getLocale(SUCCESSFULLY_RENAME, this.successfullyRename, section);
		this.cantAfford = painting.getLocale(CANT_AFFORD, this.cantAfford, section);
		this.charged = painting.getLocale(CHARGED, this.charged, section);
	}
}
