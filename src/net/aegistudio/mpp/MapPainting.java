package net.aegistudio.mpp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import net.aegistudio.mpp.brush.Pencil;
import net.aegistudio.mpp.canvas.CanvasManager;
import net.aegistudio.mpp.canvas.ChangeModeCommand;
import net.aegistudio.mpp.canvas.ChangeOwnerCommand;
import net.aegistudio.mpp.canvas.CreateCanvasCommand;
import net.aegistudio.mpp.canvas.DestroyCanvasCommand;
import net.aegistudio.mpp.palette.PaletteManager;
import net.aegistudio.mpp.palette.PigmentCommand;

/** Use blackboard pattern in this class. **/
public class MapPainting extends JavaPlugin {
	/** Other modules could get canvases from this map. **/
	public CanvasManager canvas;
	
	/** Other modules can register commands to this handle. **/
	public CompositeHandle command;
	
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
						
			// Load paint tools.
			tool = new PaintToolManager();
			tool.toolMap.put("pencil", new Pencil());
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
