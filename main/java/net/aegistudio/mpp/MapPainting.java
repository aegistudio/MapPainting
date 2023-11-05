package net.aegistudio.mpp;

import com.google.common.collect.Lists;
import net.aegistudio.mpp.algo.*;
import net.aegistudio.mpp.canvas.*;
import net.aegistudio.mpp.color.ColorManager;
import net.aegistudio.mpp.color.ExpertColorParser;
import net.aegistudio.mpp.color.RgbColorParser;
import net.aegistudio.mpp.control.ControlCommand;
import net.aegistudio.mpp.export.AssetService;
import net.aegistudio.mpp.export.PluginCanvasService;
import net.aegistudio.mpp.export.PluginCommandService;
import net.aegistudio.mpp.factory.CloneSubCommand;
import net.aegistudio.mpp.factory.NormalSubCommand;
import net.aegistudio.mpp.foreign.PluginCanvasManager;
import net.aegistudio.mpp.foreign.PluginCommandManager;
import net.aegistudio.mpp.paint.PaintManager;
import net.aegistudio.mpp.paint.MixPaintBottleCommand;
import net.aegistudio.mpp.tool.*;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.*;

// entry point
public class MapPainting extends JavaPlugin {
	
	// manages the canvases?
	public CanvasManager m_canvasManager;
    
	// command handlers?
	public CompositeHandle m_commandHandler;
    public ConfirmCommand m_commandConfirmHandler;
    public CreateCanvasCommand m_commandCreateHandler;
    public ControlCommand m_commandControlHandler;
    
    // declare managers
    public PaintToolManager m_toolManager;
    public PaintManager m_paintManager;
    public ColorManager m_colorManager;
    public PluginCanvasManager m_foreignCanvasManager;
    public PluginCommandManager m_foreignCommandManager;
    public AssetManager m_assetManager;
    
    public ConfigurationSection m_configSection;
    public Properties defaultLocale;

    
    public static final ArrayList<String> m_localeStrings = Lists.newArrayList("en");;
    
    // economy related variables
    public static final String COST_CREATE = "costCreate";
    public int costCreate = 1000;
    public static final String COST_CLONE = "costClone";
    public int costClone = 250;
    public static final String COST_COPY = "costCopy";
    public int costCopy = 500;
    public static final String COST_RENAME = "costRename";
    public int costRename = 100;
    public static final String COST_PAINTBASIC = "costPaintBasic";
    public int costPaintBasic = 250;
    public static final String COST_PAINTRGB = "costPaintRGB";
    public int costPaintRGB = 1000;
    private static Economy econ;
    
    public static final String LISTING_TITLE = "listing";
    public String listing = "@composite.listing";
    public static final String NEXT_PAGE = "nextPage";
    public String nextPage = "@composite.nextPage";
    public static final String LAST_PAGE = "lastPage";
    public String lastPage = "@composite.lastPage";
    public static final String COMMANDS_PER_PAGE = "commandsPerPage";
    public int commandsPerPage = 15;
    
    public static final String CANVAS = "canvas";
    public static final String COMMAND_LOCALE = "command";
    public static final String PAINT_TOOL = "tool";
    public static final String PALETTE = "palette";
    public static final String COLOR = "color";
    public static final String FAST_TARGET = "fastTarget";
    public String fastTarget = ".";
    
    public static final String CONFIRM = "confirm";

    // base constructor
    public MapPainting() {
    	
    	// initialise the assets
    	m_assetManager = new AssetManager();
        this.m_assetManager.put("line", new DdaLineGenerator());
        this.m_assetManager.put("fill", new ScanFloodFillGenerator());
        this.m_assetManager.put("char", new SpriteCharGenerator(MinecraftFont.Font));
        this.m_assetManager.group("string");
        this.m_assetManager.put("string.left", new StringLineGenerator(this.m_assetManager.get("char", CharacterGenerator.class)));
        this.m_assetManager.put("string.center", new MidAlignStringGenerator(this.m_assetManager.get("char", CharacterGenerator.class)));
        this.m_assetManager.put("string.right", new RightAlignStringGenerator(this.m_assetManager.get("char", CharacterGenerator.class)));
        
        m_foreignCanvasManager = new PluginCanvasManager(this);
        
    }

    // run when the plugin starts up
    @Override
    public void onEnable() {
    	
    	// load configuration settings
        try {
            InputStream inputStream;
            String locale = Locale.getDefault().toString();
            if (!m_localeStrings.contains(locale)) {
                locale = m_localeStrings.get(0);
            }
            if ((inputStream = getClass().getResourceAsStream(locale + ".properties")) == null) {
                inputStream = getClass().getResourceAsStream("/" + locale + ".properties");
            }
            this.defaultLocale = new Properties();
            this.defaultLocale.load(inputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        sendConsole("Preparing to load configuration files");
        
        try {
        	
        	// register command event handlers
            FileConfiguration config = this.getConfig();
            
            this.m_commandHandler = new CompositeHandle();
            this.m_commandCreateHandler = new CreateCanvasCommand(); // needed to load strings
            //this.command.add("create", this.create); // paint create [painting name] [1-128] (1000g)
            this.m_commandHandler.add("destroy", new DestroyCanvasCommand());
            this.m_commandHandler.add("clone", new GiveCanvasCommand());
            this.m_commandHandler.add("changeowner", new ChangeOwnerCommand());
            this.m_commandHandler.add("managepainters", new ChangeModeCommand());
            this.m_commandHandler.add("rename", new RenameCommand());
            this.m_commandHandler.add("list", new ListCanvasCommand());
            this.m_commandHandler.add("info", new InfoCommand());
            this.m_commandHandler.add("undo", new UndoCommand());
            this.m_commandHandler.add("redo", new RedoCommand());
            //this.control = new ControlCommand();
            //this.command.add("control", this.control); // dont need control do we?
            this.m_commandHandler.add("buy", new MixPaintBottleCommand());
            //this.command.add("purge", new PurgeCanvasCommand());
            
            // changes to match preferred syntax
            this.m_commandHandler.add("copy", new CloneSubCommand());
            this.m_commandHandler.add("create", new NormalSubCommand());
            
            
            this.m_commandConfirmHandler = new ConfirmCommand();
            this.m_commandHandler.add(CONFIRM, this.m_commandConfirmHandler);
            
            //this.create.add("normal", new NormalSubCommand());
            //this.create.add("wrap", new WrapSubCommand());
            //this.create.add("clone", new CloneSubCommand());
            //this.create.add("script", new ScriptSubCommand());
            //this.control.add("tap", new TapControlCommand());
            //this.control.add("wrap", new WrapControlCommand());
            //this.control.add("debug", new ScriptDebugCommand());
            if (!config.contains(COMMAND_LOCALE)) {
                config.createSection(COMMAND_LOCALE);
            }
            this.m_commandHandler.load(this, config.getConfigurationSection(COMMAND_LOCALE));
            m_configSection = config.getConfigurationSection(COMMAND_LOCALE);
            this.listing = this.getLocale(LISTING_TITLE, this.listing, m_configSection);
            this.nextPage = this.getLocale(NEXT_PAGE, this.nextPage, m_configSection);
            this.lastPage = this.getLocale(LAST_PAGE, this.lastPage, m_configSection);
            
            // TODO - BETTER FIX FOR THIS DIRTY FIX TO LOADING CONFIG MESSAGES FOR SUB COMMANDS OF PAINTINGS
            // effectively this is jsut running the config stage for the create sub command to load the output messages
            this.m_commandCreateHandler.load(this, m_configSection);
            
            // sets how many rows of commands show when being listed
            if (m_configSection.contains(COMMANDS_PER_PAGE)) {
                this.commandsPerPage = m_configSection.getInt(COMMANDS_PER_PAGE);
            } else {
                m_configSection.set(COMMANDS_PER_PAGE, this.commandsPerPage);
            }

            // Vault based economy hookup
            if (!setupEconomy() ) {
            	sendConsole("No Vault based economy found, command costs are disabled");
                //return;
            } else {
            	sendConsole("Vault based economy found, enabling command costs");
            	configureEconomy(m_configSection,this);
            }
            
            this.fastTarget = this.getLocale(FAST_TARGET, this.fastTarget, m_configSection);
            
            // configure tools
            this.m_toolManager = new PaintToolManager();
            this.m_toolManager.toolMap.put("pencil", new Pencil());
            this.m_toolManager.toolMap.put("paintBucket", new PaintBucket());
            this.m_toolManager.toolMap.put("razor", new Razor());
            this.m_toolManager.toolMap.put("framer", new Framer());
            this.m_toolManager.toolMap.put("namer", new Namer());
            if (!config.contains(PAINT_TOOL)) {
                config.createSection(PAINT_TOOL);
            }
            this.m_toolManager.load(this, config.getConfigurationSection(PAINT_TOOL));
            
            // Instantiate a paint manager, responsible for paint based items including their recipes and reliable creation
            this.m_paintManager = new PaintManager();
            if (!config.contains(PALETTE)) {
                config.createSection(PALETTE);
            }
            this.m_paintManager.load(this, config.getConfigurationSection(PALETTE));
            
            // configure colors
            this.m_colorManager = new ColorManager();
            this.m_colorManager.parsers.put("expert", new ExpertColorParser());
            this.m_colorManager.parsers.put("rgb", new RgbColorParser());
            if (!config.contains(COLOR)) {
                config.createSection(COLOR);
            }
            this.m_colorManager.load(this, config.getConfigurationSection(COLOR));
            
            // foreign command management?
            this.m_foreignCanvasManager.reset();
            this.getServer().getServicesManager().register(PluginCanvasService.class, this.m_foreignCanvasManager, this, ServicePriority.Normal);
            this.m_foreignCommandManager = new PluginCommandManager(this);
            this.getServer().getServicesManager().register(PluginCommandService.class, this.m_foreignCommandManager, this, ServicePriority.Normal);
            this.getServer().getServicesManager().register(AssetService.class, this.m_assetManager, this, ServicePriority.Normal);
            
            // configure canvases
            this.m_canvasManager = new CanvasManager();
            if (!config.contains(CANVAS)) {
                config.createSection(CANVAS);
            }
            this.m_canvasManager.load(this, config.getConfigurationSection(CANVAS));
            
            /*
             * USED TO CHECK IF OLD SHORT ISSUES STILL EXIST
             * 
             * 
            try {
            	
            	for (int i=5; i<= 80000; i++){
            		this.getServer().createMap(this.getServer().getWorlds().get(0));
            		sendConsole( i  + " of 80000" );
            	}
            	
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            
            /*
            
            if (this.getServer().getWorld("PaintingStorage") == null) {
                WorldCreator wc = new WorldCreator("PaintingStorage");
                wc.environment(World.Environment.NORMAL);
                wc.type(WorldType.FLAT);
                wc.generatorSettings("2;0;1;");
                wc.createWorld();
                
            }
            
            */

            // save the configuration? why save when we load?
            // SAVES ANY NEW VALUES INPUT BY CONFIG
            this.saveConfig();
            sendConsole("Configuration file saved");

        }
        catch (Exception e) {
            e.printStackTrace();
            this.setEnabled(false);
        }
        
        sendConsole("Plugin enabled");
        
        this.defaultLocale = null;
    }

    public MapCanvasRegistry getCanvas(String name, CommandSender sender) {
        if (this.fastTarget.equals(name)) {
            name = this.m_canvasManager.latest.get(sender.getName());
        }
        if (name == null) {
            return null;
        }
        return this.m_canvasManager.nameCanvasMap.get(name);
    }

    public void ackHistory(MapCanvasRegistry registry, CommandSender sender) {
        this.m_canvasManager.latest.put(sender.getName(), registry.name);
    }
    
    // Vault hooks
    private boolean setupEconomy() {
    	
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static Economy getEconomy() {
        return econ;
    }
    
    private void configureEconomy(ConfigurationSection locale, MapPainting plugin) {
    	
    	// create values if they do not exist, load otherwise
        if (locale.contains(COST_CREATE)) {
            plugin.costCreate = locale.getInt(COST_CREATE);
        } else {
            locale.set(COST_CREATE, plugin.costCreate);
        }
        
        // create values if they do not exist, load otherwise
        if (locale.contains(COST_CLONE)) {
            plugin.costClone = locale.getInt(COST_CLONE);
        } else {
            locale.set(COST_CLONE, plugin.costClone);
        }

        // create values if they do not exist, load otherwise
        if (locale.contains(COST_COPY)) {
            plugin.costCopy = locale.getInt(COST_COPY);
        } else {
            locale.set(COST_COPY, plugin.costCopy);
        }
        
        // create values if they do not exist, load otherwise
        if (locale.contains(COST_RENAME)) {
            plugin.costRename = locale.getInt(COST_RENAME);
        } else {
            locale.set(COST_RENAME, plugin.costRename);
        }
        
        // create values if they do not exist, load otherwise
        if (locale.contains(COST_PAINTBASIC)) {
            plugin.costPaintBasic = locale.getInt(COST_PAINTBASIC);
        } else {
            locale.set(COST_PAINTBASIC, plugin.costPaintBasic);
        }
        
        // create values if they do not exist, load otherwise
        if (locale.contains(COST_PAINTRGB)) {
            plugin.costPaintRGB = locale.getInt(COST_PAINTRGB);
        } else {
            locale.set(COST_PAINTRGB, plugin.costPaintRGB);
        }
        
        // update the plugins locale with updated config
        plugin.m_configSection = locale;
    	
    }

    
    
    private void sendConsole(String message) {
        ConsoleCommandSender console = this.getServer().getConsoleSender();
        console.sendMessage("[" + this.getName() + "] " + message);
    }

    
    
    public String getLocale(String name, String defaultLocale, ConfigurationSection section) {
        if (section.contains(name)) {
            return section.getString(name);
        }
        if (defaultLocale.charAt(0) == '@' && this.defaultLocale != null) {
            String fetchedLocale = this.defaultLocale.getProperty(defaultLocale.substring(1));
            defaultLocale = fetchedLocale == null ? defaultLocale : fetchedLocale;
            for (ChatColor chat : ChatColor.values()) {
                defaultLocale = defaultLocale.replace("${" + chat.name() + "}", chat.toString());
            }
        }
        section.set(name, defaultLocale);
        return defaultLocale;
    }

    
    
    public void onDisable() {
        try {
            this.reloadConfig();

            Configuration config = this.getConfig();
            if(!config.contains(PAINT_TOOL)) config.createSection(PAINT_TOOL);
            m_toolManager.save(this, config.getConfigurationSection(PAINT_TOOL));

            if(!config.contains(PALETTE)) config.createSection(PALETTE);
            m_paintManager.save(this, config.getConfigurationSection(PALETTE));

            if(!config.contains(COLOR)) config.createSection(COLOR);
            m_colorManager.load(this, config.getConfigurationSection(COLOR));

            config.set(CANVAS, null);
            if(!config.contains(CANVAS)) config.createSection(CANVAS);
            m_canvasManager.save(this, config.getConfigurationSection(CANVAS));

            if(!config.contains(COMMAND_LOCALE)) config.createSection(COMMAND_LOCALE);
            this.m_commandHandler.save(this, config.getConfigurationSection(COMMAND_LOCALE));

            this.saveConfig();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (command.getName().equals("paint")) {
            if (arguments.length == 0 || !arguments[0].equalsIgnoreCase(CONFIRM)) {
                this.m_commandConfirmHandler.remove(sender);
            }
            return this.m_commandHandler.handle(this, "/paint", sender, arguments);
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] arguments) {
        CompositeHandle base = null;
        ArrayList<String> complete = new ArrayList<>();
        try {
            if(command.getName().equals("paint"))
                base = this.m_commandHandler;/*
            else if(command.getName().equals("mppctl"))
                base = this.control;*/
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
                for(Map.Entry<String, MapCanvasRegistry> entry : m_canvasManager.nameCanvasMap.entrySet()) {
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
            return new ArrayList<>();
        }
    }
}

