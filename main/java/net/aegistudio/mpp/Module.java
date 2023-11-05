
package net.aegistudio.mpp;

import org.bukkit.configuration.ConfigurationSection;

public interface Module {
    public void load(MapPainting plugin, ConfigurationSection config) throws Exception;
    public void save(MapPainting plugin, ConfigurationSection config) throws Exception;
}

