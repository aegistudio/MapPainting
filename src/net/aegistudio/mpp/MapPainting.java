package net.aegistudio.mpp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import net.aegistudio.mpp.brush.Pencil;
import net.aegistudio.mpp.canvas.CanvasManager;
import net.aegistudio.mpp.canvas.CreateCanvasCommand;
import net.aegistudio.mpp.canvas.UnbindCanvasCommand;

/** Use blackboard pattern in this class. **/
public class MapPainting extends JavaPlugin {
	/** Other modules could get canvases from this map. **/
	public CanvasManager canvas;
	
	/** Other modules can register commands to this handle. **/
	public CompositeHandle command;
	
	/** Other modules can invoke painting to this paint tool **/
	public PaintToolManager tool;
	
	/** Keyword for configurations **/
	public static final String CANVAS = "canvas";
	public static final String COMMAND_LOCALE = "command";
	public static final String PAINT_TOOL = "tool";
	
	public void onEnable() {
		try {
			// Load handle.
			Configuration config = this.getConfig();
			this.command = new CompositeHandle();

			command.add("create", new CreateCanvasCommand());
			command.add("unbind", new UnbindCanvasCommand());
			
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
			config.set(CANVAS, null);
			if(!config.contains(CANVAS)) config.createSection(CANVAS);
			canvas.save(this, config.getConfigurationSection(CANVAS));
			
			if(!config.contains(PAINT_TOOL)) config.createSection(PAINT_TOOL);
			tool.save(this, config.getConfigurationSection(PAINT_TOOL));
			
			this.saveConfig();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
		if(command.getName().equals("mpp")) {
			sender.sendMessage("");
			return this.command.handle(this, "/mpp", sender, arguments);
		}
		return false;
	}
}
