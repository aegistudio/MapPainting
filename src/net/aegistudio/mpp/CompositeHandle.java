package net.aegistudio.mpp;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class CompositeHandle extends ActualHandle {
	public final ArrayList<String> name = new ArrayList<String>();
	public final ArrayList<CommandHandle> subcommand = new ArrayList<CommandHandle>();

	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		if(arguments.length == 0) {
			sender.sendMessage("Showing " + ChatColor.BOLD + "subcommands" + ChatColor.RESET 
					+ " for " + ChatColor.YELLOW + prefix + ChatColor.RESET + ":");
			for(int i = 0; i < name.size(); i ++) {
				StringBuilder builder = new StringBuilder();
				builder.append(ChatColor.YELLOW);
				builder.append(prefix);
				builder.append(' ');
				builder.append(ChatColor.BOLD);
				builder.append(name.get(i));
				builder.append(ChatColor.RESET);
				builder.append(": ");
				builder.append(subcommand.get(i).description());
				sender.sendMessage(new String(builder));
			}
			return true;
		}
		else {
			CommandHandle handle = null;
			for(int i = 0; i < name.size(); i ++) 
				if(name.get(i).equalsIgnoreCase(arguments[0])) {
					handle = subcommand.get(i);
					break;
				}

			if(handle == null) return false;
			String[] passUnder = new String[arguments.length - 1];
			System.arraycopy(arguments, 1, passUnder, 0, arguments.length - 1);
			return handle.handle(painting, prefix.concat(" ")
					.concat(arguments[0]), sender, passUnder);
		}
	}

	public static final String DESCRIPTION = "description";
	public static final String SUBCOMMAND = "subcommand";
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		
		if(!section.contains(SUBCOMMAND)) 
			section.createSection(SUBCOMMAND);
		
		ConfigurationSection subcommand = section.getConfigurationSection(SUBCOMMAND);
		for(int i = 0; i < this.name.size(); i ++) {
			if(!subcommand.contains(this.name.get(i)))
				subcommand.createSection(this.name.get(i));
			
			this.subcommand.get(i).load(painting, 
					subcommand.getConfigurationSection(this.name.get(i)));
		}
	}

	public void add(String name, CommandHandle command) {
		this.name.add(name);
		this.subcommand.add(command);
	}
}
