
package net.aegistudio.mpp;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ActualHandle
implements CommandHandle {
    public String description;
    public static final String DESCRIPTION = "description";

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public abstract boolean handle(MapPainting var1, String var2, CommandSender sender, String[] args);

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        if (this.description != null) {
            this.description = painting.getLocale("description", this.description, section);
        }
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
    }
}

