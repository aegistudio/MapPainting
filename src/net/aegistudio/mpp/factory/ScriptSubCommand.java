package net.aegistudio.mpp.factory;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.script.ScriptCanvas;
import net.aegistudio.mpp.script.ScriptEnginePool;
import net.aegistudio.mpp.script.UnsupportedException;

public class ScriptSubCommand extends ConcreteCreateSubCommand {
	{ description = "@create.script.description"; paramList = "<script> [<argument>...]"; }
	
	public static final String NO_SCRIPT = "noScript";
	public String noScript = "@create.script.noScript";
	
	public static final String LANGUAGE_UNSUPPORTED = "languageUnsupported";
	public String languageUnsupported = "@create.script.languageUnsupported";
	
	public static final String PROMPT_ENGINE = "promptEngine";
	
	@Override
	protected Canvas create(MapPainting painting, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage(noScript);
			return null;
		}
		else {
			try {
				ScriptCanvas canvas = new ScriptCanvas(painting);
				canvas.filename = arguments[0];
				canvas.setEngine();
				canvas.setScript();

				String[] subarguments = new String[arguments.length - 1];
				System.arraycopy(arguments, 1, subarguments, 0, arguments.length - 1);
				canvas.reboot(subarguments);
				return canvas;
			}
			catch(UnsupportedException e) {
				sender.sendMessage(languageUnsupported
						.replace("$language", e.getMessage()));
				return null;
			}
			catch(Exception e) {
				sender.sendMessage(e.toString());
				return null;
			}
		}
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.noScript = painting.getLocale(NO_SCRIPT, noScript, section);
		this.languageUnsupported = painting.getLocale(LANGUAGE_UNSUPPORTED, languageUnsupported, section);
		if(!section.contains(PROMPT_ENGINE)) section.createSection(PROMPT_ENGINE);
		
		ConfigurationSection promptEngines = section.getConfigurationSection(PROMPT_ENGINE);
		for(String key : promptEngines.getKeys(false)) 
			ScriptEnginePool.replace(key, promptEngines.getString(key));
	}
}
