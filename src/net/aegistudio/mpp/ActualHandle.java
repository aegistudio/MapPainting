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
			description = this.getLocale(DESCRIPTION, description, section);
	}
	
	protected String getLocale(String name, String defaultLocale, ConfigurationSection section) {
		if(section.contains(name))
			return section.getString(name);
		else {
			section.set(name, defaultLocale);
			return defaultLocale;
		}
	}
	
	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception{
		
	}
}
