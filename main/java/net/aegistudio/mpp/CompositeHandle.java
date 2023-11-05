
package net.aegistudio.mpp;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class CompositeHandle extends ActualHandle {
    public final ArrayList<String> name = new ArrayList<String>();
    public final ArrayList<CommandHandle> subcommand = new ArrayList<CommandHandle>();
    public static final String DESCRIPTION = "description";
    public static final String SUBCOMMAND = "subcommand";
    
    // lists all the commands that are declared for the plugin
    public void listCommand(MapPainting plugin, String prefix, CommandSender sender, int page) {
        
    	sender.sendMessage(plugin.listing.replace("$prefix", prefix));
        
        int beginIndex = (page - 1) * plugin.commandsPerPage;
        if (beginIndex >= this.subcommand.size() || beginIndex < 0) {
            sender.sendMessage(plugin.lastPage.replace("$prefix", prefix));
        } else {
            int endIndex = Math.min(this.name.size(), beginIndex + plugin.commandsPerPage);
            for (int i = beginIndex; i < endIndex; ++i) {
                StringBuilder builder = new StringBuilder();
                builder.append((Object)ChatColor.YELLOW);
                builder.append(prefix);
                builder.append(' ');
                builder.append((Object)ChatColor.BOLD);
                builder.append(this.name.get(i));
                builder.append((Object)ChatColor.RESET);
                builder.append(": ");
                builder.append(this.subcommand.get(i).description());
                sender.sendMessage(new String(builder));
            }
            if (endIndex == this.name.size()) {
                sender.sendMessage(plugin.lastPage.replace("$prefix", prefix));
            } else {
                sender.sendMessage(plugin.nextPage.replace("$prefix", prefix).replace("$nextPage", Integer.toString(page + 1)));
            }
        }
    }

    @Override
    public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            this.listCommand(painting, prefix, sender, 1);
            return true;
        }
        CommandHandle handle = null;
        for (int i = 0; i < this.name.size(); ++i) {
            if (!this.name.get(i).equalsIgnoreCase(arguments[0])) continue;
            handle = this.subcommand.get(i);
            break;
        }
        if (handle == null) {
            try {
                int page = Integer.parseInt(arguments[0]);
                this.listCommand(painting, prefix, sender, page);
                return true;
            }
            catch (Throwable t) {
                return false;
            }
        }
        String[] passUnder = new String[arguments.length - 1];
        System.arraycopy(arguments, 1, passUnder, 0, arguments.length - 1);
        return handle.handle(painting, prefix.concat(" ").concat(arguments[0]), sender, passUnder);
    }

    @Override
    public void load(MapPainting painting, ConfigurationSection section) throws Exception {
        super.load(painting, section);
        if (!section.contains(SUBCOMMAND)) {
            section.createSection(SUBCOMMAND);
        }
        ConfigurationSection subcommand = section.getConfigurationSection(SUBCOMMAND);
        for (int i = 0; i < this.name.size(); ++i) {
            if (!subcommand.contains(this.name.get(i))) {
                subcommand.createSection(this.name.get(i));
            }
            this.subcommand.get(i).load(painting, subcommand.getConfigurationSection(this.name.get(i)));
        }
    }

    public void add(String name, CommandHandle command) {
        this.name.add(name);
        this.subcommand.add(command);
    }

    @Override
    public void save(MapPainting painting, ConfigurationSection section) throws Exception {
        super.save(painting, section);
        for (CommandHandle handle : this.subcommand) {
            handle.save(painting, section);
        }
    }
}

