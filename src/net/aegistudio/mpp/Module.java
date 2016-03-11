package net.aegistudio.mpp;

import org.bukkit.configuration.ConfigurationSection;

public interface Module {
	public void load(MapPainting painting, ConfigurationSection section) throws Exception;
	
	public void save(MapPainting painting, ConfigurationSection section) throws Exception;
}
