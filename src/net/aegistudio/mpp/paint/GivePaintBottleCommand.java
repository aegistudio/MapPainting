
package net.aegistudio.mpp.paint;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.aegistudio.mpp.MapPainting;

import java.awt.*;


public class GivePaintBottleCommand implements CommandExecutor {
	
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	// Guard against insufficient permissions to forcibly give a paint bottle
        if (!sender.hasPermission("palette.command")) {
            sender.sendMessage("\u00a7cYou don't have permission to execute this command.");
            return true;
        }
        
        // Guard against too few arguments to generate a bottle
        if (args.length < 4) {
            sender.sendMessage("\u00a7cUsage: /palette <player> <red> <green> <blue> <optional: amount>");
            return true;
        }
        
        // Guard against giving items to offline players
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("\u00a7cThis player is offline.");
            return true;
        }
        
        // Guard against invalid channel values
        int red;
        int green;
        int blue;
        try {
            red = Integer.parseInt(args[1]);
            green = Integer.parseInt(args[2]);
            blue = Integer.parseInt(args[3]);
        }
        catch (Exception e) {
            sender.sendMessage("\u00a7cInvalid number.");
            return true;
        }
        
        // Guard against invalid quantities of bottles
        int amount = 1;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
            }
            catch (Exception e) {
                sender.sendMessage("\u00a7cInvalid number.");
                return true;
            }
        }
        
        if (!(this.isBetween(red, 0, 255) && this.isBetween(green, 0, 255) && this.isBetween(blue, 0, 255))) {
            sender.sendMessage("\u00a7cRGB values must be between 0 to 255.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage("\u00a7cThe amount must be bigger than 1.");
            return true;
        }
        for (int i = 0; i < amount; ++i) {
        	MapPainting plugin = (MapPainting)sender.getServer().getPluginManager().getPlugin("MapPaintingRebuild"); 
        	PaintManager PM = plugin.m_paintManager;
            player.getInventory().addItem(PM.getPaintBottle(new Color(red, green, blue)));
        }
        sender.sendMessage("\u00a7aSuccessfully gave \u00a7e" + player.getName() + "\u00a7a the color.");
        return true;
    }

    private boolean isBetween(int num, int min, int max) {
        return num >= min && num <= max;
    }
}

