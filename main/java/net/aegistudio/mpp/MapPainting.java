package net.aegistudio.mpp;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;
import com.google.common.collect.Lists;

import net.aegistudio.mpp.mcinject.CraftMinecraftServer;
import net.aegistudio.mpp.mcinject.MinecraftServer;
import net.aegistudio.mpp.algo.CharacterGenerator;
import net.aegistudio.mpp.algo.DdaLineGenerator;
import net.aegistudio.mpp.algo.MidAlignStringGenerator;
import net.aegistudio.mpp.algo.RightAlignStringGenerator;
import net.aegistudio.mpp.algo.ScanFloodFillGenerator;
import net.aegistudio.mpp.algo.SpriteCharGenerator;
import net.aegistudio.mpp.algo.StringLineGenerator;
import net.aegistudio.mpp.canvas.CanvasManager;
import net.aegistudio.mpp.canvas.ChangeModeCommand;
import net.aegistudio.mpp.canvas.ChangeOwnerCommand;
import net.aegistudio.mpp.canvas.CreateCanvasCommand;
import net.aegistudio.mpp.canvas.DestroyCanvasCommand;
import net.aegistudio.mpp.canvas.GiveCanvasCommand;
import net.aegistudio.mpp.canvas.InfoCommand;
import net.aegistudio.mpp.canvas.ListCanvasCommand;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;
import net.aegistudio.mpp.canvas.PurgeCanvasCommand;
import net.aegistudio.mpp.canvas.RenameCommand;
import net.aegistudio.mpp.color.ColorManager;
import net.aegistudio.mpp.color.ExpertColorParser;
import net.aegistudio.mpp.color.RgbColorParser;
import net.aegistudio.mpp.control.ControlCommand;
import net.aegistudio.mpp.control.TapControlCommand;
import net.aegistudio.mpp.control.WrapControlCommand;
import net.aegistudio.mpp.export.AssetService;
import net.aegistudio.mpp.export.PluginCanvasService;
import net.aegistudio.mpp.export.PluginCommandService;
import net.aegistudio.mpp.factory.CloneSubCommand;
import net.aegistudio.mpp.factory.NormalSubCommand;
import net.aegistudio.mpp.factory.ScriptSubCommand;
import net.aegistudio.mpp.factory.WrapSubCommand;
import net.aegistudio.mpp.foreign.PluginCanvasManager;
import net.aegistudio.mpp.foreign.PluginCommandManager;
import net.aegistudio.mpp.palette.PaletteManager;
import net.aegistudio.mpp.palette.PigmentCommand;
import net.aegistudio.mpp.script.ScriptDebugCommand;
import net.aegistudio.mpp.tool.PaintBucket;
import net.aegistudio.mpp.tool.Pencil;
import net.aegistudio.mpp.tool.Razor;
import net.aegistudio.mpp.tool.RedoCommand;
import net.aegistudio.mpp.tool.UndoCommand;

/** Use blackboard pattern in this class. **/
public class MapPainting extends JavaPlugin {
	/** Other modules could get canvases from this map. **/
	public CanvasManager canvas;
	public MinecraftServer inject;
	
	/** Other modules can register commands to this handle. **/
	public CompositeHandle command;
	
	/** These configurations are used when composite commands need. **/
	public static final String LISTING_TITLE = "listing";
	public String listing = "@composite.listing";
	
	public static final String NEXT_PAGE = "nextPage";
	public String nextPage = "@composite.nextPage";
	
	public static final String LAST_PAGE = "lastPage";
	public String lastPage = "@composite.lastPage";
	
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
	
	/** Fast when specify canvas name **/
	public static final String FAST_TARGET = "fastTarget";
	public String fastTarget = ".";
	public MapCanvasRegistry getCanvas(String name, CommandSender sender) {
		if(fastTarget.equals(name)) name = canvas.latest.get(sender.getName());
		if(name == null) return null;
		else return canvas.nameCanvasMap.get(name);
	}
	
	public void ackHistory(MapCanvasRegistry registry, CommandSender sender) {
		canvas.latest.put(sender.getName(), registry.name);
	}
	
	public Properties defaultLocale;
	
	/** Foreign plugins love this most! **/
	public PluginCanvasManager foreignCanvas = new PluginCanvasManager(this);
	public PluginCommandManager foreignCommand;
	public AssetManager asset = new AssetManager(); {
		asset.put("line", new DdaLineGenerator());
		asset.put("fill", new ScanFloodFillGenerator());
		asset.put("char", new SpriteCharGenerator(MinecraftFont.Font));
		
		asset.group("string");
		asset.put("string.left", new StringLineGenerator(asset.get("char", CharacterGenerator.class)));
		asset.put("string.center", new MidAlignStringGenerator(asset.get("char", CharacterGenerator.class)));
		asset.put("string.right", new RightAlignStringGenerator(asset.get("char", CharacterGenerator.class)));
	}
	
	public static final ArrayList<String> supportedLocale = Lists.newArrayList("en");
	
	public void onEnable() {
		try {
			String locale = Locale.getDefault().toString();
			if(supportedLocale.indexOf(locale) < 0) locale = supportedLocale.get(0);
			
			InputStream inputStream = getClass().getResourceAsStream(locale + ".properties");
			if(inputStream == null) inputStream = getClass()
					.getResourceAsStream("/" + locale + ".properties");
			defaultLocale = new Properties();
			defaultLocale.load(inputStream);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {

			
			// Load handle.
			Configuration config = this.getConfig();
			this.command = new CompositeHandle();

			command.add("create", create = new CreateCanvasCommand());
			command.add("destroy", new DestroyCanvasCommand());
			command.add("give", new GiveCanvasCommand());
			command.add("chown", new ChangeOwnerCommand());
			command.add("chmod", new ChangeModeCommand());
			command.add("rename", new RenameCommand());
			command.add("list", new ListCanvasCommand());
			command.add("info", new InfoCommand());
			command.add("undo", new UndoCommand());
			command.add("redo", new RedoCommand());
			command.add("control", control = new ControlCommand());
			command.add("pigment", new PigmentCommand());
			command.add("purge", new PurgeCanvasCommand());
			command.add(CONFIRM, this.hazard = new ConfirmCommand());
			
			// Load create commands.
			this.create.add("normal", new NormalSubCommand());
			this.create.add("wrap", new WrapSubCommand());
			this.create.add("clone", new CloneSubCommand());
			this.create.add("script", new ScriptSubCommand());
			
			// Load control commands.
			this.control.add("tap", new TapControlCommand());
			this.control.add("wrap", new WrapControlCommand());
			this.control.add("debug", new ScriptDebugCommand());
			
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
			
			fastTarget = this.getLocale(FAST_TARGET, fastTarget, locale);
			
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
			
			// Load foreign plugin canvas service.
			this.foreignCanvas.reset();
			this.getServer().getServicesManager().register(PluginCanvasService.class, 
					this.foreignCanvas, this, ServicePriority.Normal);
			
			// Load foreign command service.
			this.foreignCommand = new PluginCommandManager(this);
			this.getServer().getServicesManager().register(PluginCommandService.class, this.foreignCommand,
					this, ServicePriority.Normal);
			
			// Load foregin algorithm service.
			this.getServer().getServicesManager().register(AssetService.class, this.asset,
					this, ServicePriority.Normal);
			
			// Load map.
			this.canvas = new CanvasManager();
			if(!config.contains(CANVAS)) config.createSection(CANVAS);
			this.canvas.load(this, config.getConfigurationSection(CANVAS));
			
			this.saveConfig();
			
			// Load packet sender.
			this.inject = new CraftMinecraftServer(getServer());
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
	    
	    /**
	     * Check whether there's new version of this plugin.
	     */
		new Thread(() -> {
	    	String currentVersion = this.getDescription().getVersion();
		    try {
		    	URL masterUrl = new URL("https://raw.githubusercontent.com/aegistudio/MapPainting/master/build.properties");
		    	Properties masterBuild = new Properties();
		    	masterBuild.load(masterUrl.openConnection().getInputStream());
		    	String masterVersion = masterBuild.getProperty("version");
	
		    	if(!masterVersion.equals(currentVersion)) {
		    		sendConsole(ChatColor.AQUA + "The newest version (" + ChatColor.GREEN + masterVersion 
		    				+ ChatColor.AQUA + ") has been published!");
		    		
		    		sendConsole(ChatColor.AQUA + "Downloads: ");
		    		String downloadJava8 = masterBuild.getProperty("download.java8");
		    		if(downloadJava8 != null) sendConsole(ChatColor.AQUA + "#   java8: " + 
		    				ChatColor.GREEN + ChatColor.UNDERLINE + downloadJava8);
		    		
		    		String downloadJava7 = masterBuild.getProperty("download.java7");
		    		if(downloadJava7 != null) sendConsole(ChatColor.AQUA + "#   java7: " + 
		    				ChatColor.GREEN + ChatColor.UNDERLINE + downloadJava7);
		    		
		    		sendConsole(ChatColor.AQUA + "Forums: ");
	    			sendConsole(ChatColor.AQUA+ "#   spigotmc: " + ChatColor.GREEN
	    					+ ChatColor.UNDERLINE + "https://www.spigotmc.org/resources/19823/");
	    			sendConsole(ChatColor.AQUA + "#   mcbbs: " + ChatColor.GREEN
	    					+ ChatColor.UNDERLINE + "http://www.mcbbs.net/thread-565739-1-1.html");
		    	}
		    	else sendConsole("Congratulations! The plugin is of the newest version now!");
		    } catch(Exception e) {
		    	sendConsole("Cannot fetch information of the newest version, I'm sorry. :-(");
		    }
		}).start();
		
		defaultLocale = null;
	}
	
	private void sendConsole(String data) {
		ConsoleCommandSender console = this.getServer().getConsoleSender();
		console.sendMessage("[" + this.getName() + "] " + data);
	}
	
	public String getLocale(String name, String defaultLocale, ConfigurationSection section) {
		if(section.contains(name))
			return section.getString(name);
		else {
			if(defaultLocale.charAt(0) == '@' && this.defaultLocale != null) {
				String fetchedLocale = this.defaultLocale.getProperty(defaultLocale.substring(1));
				defaultLocale = fetchedLocale == null? defaultLocale : fetchedLocale;
				for(ChatColor chat : ChatColor.values())
					defaultLocale = defaultLocale.replace("${" + chat.name() + "}", chat.toString());
			}
			
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
			
			if(!config.contains(COMMAND_LOCALE)) config.createSection(COMMAND_LOCALE);
			this.command.save(this, config.getConfigurationSection(COMMAND_LOCALE));
			
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
		else if(command.getName().equals("mppctl")) {
			sender.sendMessage("");
			
			return this.control.handle(this, "/mppctl", sender, arguments);
		}
		return false;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
		CompositeHandle base = null;
		ArrayList<String> complete = new ArrayList<String>();
		try {
			if(command.getName().equals("mpp"))
				base = this.command;
			else if(command.getName().equals("mppctl"))
				base = this.control;
			if(base == null) return complete;
			
			CommandHandle handle = base;
			for(int i = 0; i < arguments.length - 1; i ++) {
				int index = base.name.indexOf(arguments[i]);
				if(index < 0) return complete;
				handle = base.subcommand.get(index);
				if(!(handle instanceof CompositeHandle)) break;
				base = (CompositeHandle) handle;
			}
			
			if(handle instanceof CompositeHandle) {
				// Complete the command this case.
				CompositeHandle current = (CompositeHandle) handle;
				for(int i = 0; i < current.name.size(); i ++)
					if(current.name.get(i).startsWith(arguments[arguments.length - 1]))
						complete.add(current.name.get(i));
			}
			else {
				// Complete the canvases this case.
				for(Entry<String, MapCanvasRegistry> entry : canvas.nameCanvasMap.entrySet()) {
					if(!sender.hasPermission("mpp.manager"))
						if(!entry.getValue().owner.contains(sender.getName())) continue;
					if(entry.getValue().owner.length() == 0) continue;
					
					if(entry.getKey().startsWith(arguments[arguments.length - 1]))
						complete.add(entry.getKey());
				}
			}
			return complete;
		}
		catch(Throwable t) {
			return new ArrayList<String>();
		}
	}
}
