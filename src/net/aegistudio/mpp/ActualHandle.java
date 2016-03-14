package net.aegistudio.mpp;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ActualHandle implements CommandHandle{
	public String description;
	public static final String DESCRIPTION = "description";
	
	@Override
	public String description() {
		return description;
	}

	@Override
	public abstract boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments);

	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception{
		if(description != null) 
			description = painting.getLocale(DESCRIPTION, description, section);
	}
	
	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception{
		
	}
}
