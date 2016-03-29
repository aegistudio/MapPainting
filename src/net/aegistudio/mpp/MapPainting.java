package net.aegistudio.mpp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;

import net.aegistudio.mpp.canvas.CanvasManager;
import net.aegistudio.mpp.canvas.ChangeModeCommand;
import net.aegistudio.mpp.canvas.ChangeOwnerCommand;
import net.aegistudio.mpp.canvas.CreateCanvasCommand;
import net.aegistudio.mpp.canvas.DestroyCanvasCommand;
import net.aegistudio.mpp.canvas.ListCanvasCommand;
import net.aegistudio.mpp.color.ColorManager;
import net.aegistudio.mpp.color.ExpertColorParser;
import net.aegistudio.mpp.color.RgbColorParser;
import net.aegistudio.mpp.control.ControlCommand;
import net.aegistudio.mpp.control.TapControlCommand;
import net.aegistudio.mpp.control.WrapControlCommand;
import net.aegistudio.mpp.factory.CloneSubCommand;
import net.aegistudio.mpp.factory.NormalSubCommand;
import net.aegistudio.mpp.factory.ScriptSubCommand;
import net.aegistudio.mpp.factory.WrapSubCommand;
import net.aegistudio.mpp.palette.PaletteManager;
import net.aegistudio.mpp.palette.PigmentCommand;
import net.aegistudio.mpp.tool.PaintBucket;
import net.aegistudio.mpp.tool.Pencil;
import net.aegistudio.mpp.tool.Razor;
import net.aegistudio.mpp.tool.RedoCommand;
import net.aegistudio.mpp.tool.UndoCommand;

/** Use blackboard pattern in this class. **/
public class MapPainting extends JavaPlugin {
	/** Other modules could get canvases from this map. **/
	public CanvasManager canvas;
	
	/** Other modules can register commands to this handle. **/
	public CompositeHandle command;
	
	/** These configurations are used when composite commands need. **/
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
	
	/** Other create sub-commands can register and get common configuration from create. **/
	public CreateCanvasCommand create;
	
	/** Other control sub-commands can register and get common configuration from control **/
	public ControlCommand control;
	
	/** Other modules can invoke painting to this paint tool **/
	public PaintToolManager tool;
	
	/** Other modules can invoke palette if you have coloured information **/
	public PaletteManager palette;
	
	/** Other modules can invoke color to parse color expressions.**/
	public ColorManager color;
	
	/** Keyword for configurations **/
	public static final String CANVAS = "canvas";
	public static final String COMMAND_LOCALE = "command";
	public static final String PAINT_TOOL = "tool";
	public static final String PALETTE = "palette";
	public static final String COLOR = "color";
	
	public void onEnable() {
		try {
			// Load handle.
			Configuration config = this.getConfig();
			this.command = new CompositeHandle();

			command.add("create", create = new CreateCanvasCommand());
			command.add("destroy", new DestroyCanvasCommand());
			command.add("chown", new ChangeOwnerCommand());
			command.add("chmod", new ChangeModeCommand());
			command.add("list", new ListCanvasCommand());
			command.add("undo", new UndoCommand());
			command.add("redo", new RedoCommand());
			command.add("control", control = new ControlCommand());
			command.add("pigment", new PigmentCommand());
			command.add(CONFIRM, this.hazard = new ConfirmCommand());
			
			// Load create commands.
			this.create.add("normal", new NormalSubCommand());
			this.create.add("wrap", new WrapSubCommand());
			this.create.add("clone", new CloneSubCommand());
			this.create.add("script", new ScriptSubCommand());
			
			// Load control commands.
			this.control.add("tap", new TapControlCommand());
			this.control.add("wrap", new WrapControlCommand());
			
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
			tool.toolMap.put("razor", new Razor());
			if(!config.contains(PAINT_TOOL)) config.createSection(PAINT_TOOL);
			this.tool.load(this, config.getConfigurationSection(PAINT_TOOL));
			
			// Load palette.
			palette = new PaletteManager();
			if(!config.contains(PALETTE)) config.createSection(PALETTE);
			palette.load(this, config.getConfigurationSection(PALETTE));
			
			// Load color parsers.
			color = new ColorManager();
			color.parsers.put("expert", new ExpertColorParser());
			color.parsers.put("rgb", new RgbColorParser());
			if(!config.contains(COLOR)) config.createSection(COLOR);
			color.load(this, config.getConfigurationSection(COLOR));
			
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
		
		/**
		 * Sending metric message to mcstats.
		 */
	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (Exception e) {
	        
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
			
			if(!config.contains(COLOR)) config.createSection(COLOR);
			color.load(this, config.getConfigurationSection(COLOR));
			
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
