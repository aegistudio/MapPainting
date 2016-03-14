package net.aegistudio.mpp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import net.aegistudio.mpp.canvas.CanvasManager;
import net.aegistudio.mpp.canvas.ChangeModeCommand;
import net.aegistudio.mpp.canvas.ChangeOwnerCommand;
import net.aegistudio.mpp.canvas.CreateCanvasCommand;
import net.aegistudio.mpp.canvas.DestroyCanvasCommand;
import net.aegistudio.mpp.canvas.ListCanvasCommand;
import net.aegistudio.mpp.palette.PaletteManager;
import net.aegistudio.mpp.palette.PigmentCommand;
import net.aegistudio.mpp.tool.PaintBucket;
import net.aegistudio.mpp.tool.Pencil;

/** Use blackboard pattern in this class. **/
public class MapPainting extends JavaPlugin {
	/** Other modules could get canvases from this map. **/
	public CanvasManager canvas;
	
	/** Other modules can register commands to this handle. **/
	public CompositeHandle command;
	
	public static final String LISTING_TITLE = "listing";
	public String listing = "Listing " + ChatColor.BOLD + "subcommands" + ChatColor.RESET 
			+ " for " + ChatColor.YELLOW + "$prefix" + ChatColor.RESET + ":";
	
	public static final String NEXT_PAGE = "nextPage";
	public String nextPage = "Please issue " + ChatColor.YELLOW + "$prefix $nextPage"
			+ ChatColor.RESET + " for more " + ChatColor.BOLD + "subcommands" + ChatColor.RESET + ".";
	
	public static final String LAST_PAGE = "lastPage";
	public String lastPage = "This is the last page of " + ChatColor.BOLD 
			+ "subcommands" + ChatColor.RESET + " for " + ChatColor.YELLOW 
			+ "$prefix" + ChatColor.RESET + ".";
	
	public static final String COMMANDS_PER_PAGE = "commandsPerPage";
	public int commandsPerPage = 5;
	
	/** Other modules can issue hazardous command using this. **/
	public static final String CONFIRM = "confirm";
	public ConfirmCommand hazard;
	
	/** Other modules can invoke painting to this paint tool **/
	public PaintToolManager tool;
	
	/** Other modules can invoke palette if you have coloured information **/
	public PaletteManager palette;
	
	/** Keyword for configurations **/
	public static final String CANVAS = "canvas";
	public static final String COMMAND_LOCALE = "command";
	public static final String PAINT_TOOL = "tool";
	public static final String PALETTE = "palette";
	
	public void onEnable() {
		try {
			// Load handle.
			Configuration config = this.getConfig();
			this.command = new CompositeHandle();

			command.add("create", new CreateCanvasCommand());
			command.add("destroy", new DestroyCanvasCommand());
			command.add("chown", new ChangeOwnerCommand());
			command.add("chmod", new ChangeModeCommand());
			command.add("list", new ListCanvasCommand());
			command.add("pigment", new PigmentCommand());
			command.add(CONFIRM, this.hazard = new ConfirmCommand());
			
			/*
			CompositeHandle brush = new CompositeHandle();
			brush.description = "Control brushes that is to paint strokes on canvas.";
			this.command.subcommand.put("brush", brush);
			
			CompositeHandle stamp = new CompositeHandle();
			stamp.description = "Handle stamp which is special tool to paint external images on canvas.";
			this.command.subcommand.put("stamp", stamp);
			*/

			if(!config.contains(COMMAND_LOCALE))
				config.createSection(COMMAND_LOCALE);
			this.command.load(this, config.getConfigurationSection(COMMAND_LOCALE));
			
			// Load unified locale.
			ConfigurationSection locale = config.getConfigurationSection(COMMAND_LOCALE);
			listing = this.getLocale(LISTING_TITLE, listing, locale);
			nextPage = this.getLocale(NEXT_PAGE, nextPage, locale);
			lastPage = this.getLocale(LAST_PAGE, lastPage, locale);
			if(locale.contains(COMMANDS_PER_PAGE))
				this.commandsPerPage = locale.getInt(COMMANDS_PER_PAGE);
			else locale.set(COMMANDS_PER_PAGE, this.commandsPerPage);
			
			// Load paint tools.
			tool = new PaintToolManager();
			tool.toolMap.put("pencil", new Pencil());
			tool.toolMap.put("paintBucket", new PaintBucket());
			if(!config.contains(PAINT_TOOL)) config.createSection(PAINT_TOOL);
			this.tool.load(this, config.getConfigurationSection(PAINT_TOOL));
			
			// Load palette.
			palette = new PaletteManager();
			if(!config.contains(PALETTE)) config.createSection(PALETTE);
			palette.load(this, config.getConfigurationSection(PALETTE));
			
			// Load map.
			this.canvas = new CanvasManager();
			if(!config.contains(CANVAS)) config.createSection(CANVAS);
			this.canvas.load(this, config.getConfigurationSection(CANVAS));
			
			this.saveConfig();
		}
		catch(Exception e) {
			e.printStackTrace();
			this.setEnabled(false);
		}
	}
	
	public String getLocale(String name, String defaultLocale, ConfigurationSection section) {
		if(section.contains(name))
			return section.getString(name);
		else {
			section.set(name, defaultLocale);
			return defaultLocale;
		}
	}
	
	public void onDisable() {
		try {
			this.reloadConfig();
			
			Configuration config = this.getConfig();
			if(!config.contains(PAINT_TOOL)) config.createSection(PAINT_TOOL);
			tool.save(this, config.getConfigurationSection(PAINT_TOOL));
			
			if(!config.contains(PALETTE)) config.createSection(PALETTE);
			palette.save(this, config.getConfigurationSection(PALETTE));
			
			config.set(CANVAS, null);
			if(!config.contains(CANVAS)) config.createSection(CANVAS);
			canvas.save(this, config.getConfigurationSection(CANVAS));
			
			this.saveConfig();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if(command.getName().equals("mpp")) {
			sender.sendMessage("");
			if(arguments.length == 0 || !arguments[0].equalsIgnoreCase(CONFIRM))
				hazard.remove(sender);
			
			return this.command.handle(this, "/mpp", sender, arguments);
		}
		return false;
	}
}
