package net.aegistudio.mpp.canvas;

import java.util.TreeSet;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.MapPainting;

public class InfoCommand extends ActualHandle {
	{		description = "@info.description";	}
	
	public static final String NOT_HOLDING = "notHolding";
	public String notHolding = "@info.notHolding";
	
	public static final String NAME = "name";
	public String name = "@info.name";
	
	public static final String OWNER = "owner";
	public String owner = "@info.owner";
	
	public static final String PAINTER = "painter";
	public String painter = "@info.painter";
	
	public static final String INTERACTOR = "interactor";
	public String interactor = "@info.interactor";
	
	public static final String PAINTER_LISTITEM = "painterListitem";
	public String painterListitem = "@info.painterListItem";
	
	public static final String PREVILEGE = "privilege";
	public String privilege = "@info.privilege";
	
	@Override
	public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
		MapCanvasRegistry registry = null;
		if(arguments.length == 0) {
			if(sender instanceof Player)
				registry = painting.canvas.holding((Player) sender);
		}
		else registry = painting.canvas.nameCanvasMap.get(arguments[0]);
		
		if(registry == null) {
			sender.sendMessage(notHolding);
			return true;
		}
		
		sender.sendMessage(name.replace("$name", registry.name)
				.replace("$binding", Short.toString(registry.binding)));
		
		sender.sendMessage(owner.replace("$owner", registry.owner));
		sender.sendMessage(painter.replace("$painterList", list(registry.painter)));
		sender.sendMessage(interactor.replace("$interactorList", list(registry.interactor)));
		
		Permissible testing = null;
		String testingname = null;
		if(arguments.length >= 2) {
			testingname = arguments[1];
			testing = painting.getServer().getPlayer(testingname);
		}
		else {
			testing = sender;
			testingname = sender.getName();
		}
		
		TreeSet<String> previlegeList = new TreeSet<String>();
		if(registry.owner.equals(testingname)) previlegeList.add("owner");
		if(registry.select(registry.painter, testingname, testing)) previlegeList.add("painter");
		if(registry.select(registry.interactor, testingname, testing)) previlegeList.add("interact");
		sender.sendMessage(privilege.replace("$who", testingname).replace("$privilegeList", list(previlegeList)));
		
		return true;
	}
	
	public String list(TreeSet<String> set) {
		StringBuilder painterList = new StringBuilder();
		boolean first = true;
		for(String painter : set) {
			if(first) first = false;
			else painterList.append(", ");
			painterList.append(painterListitem.replace("$painter", painter));
		}
		return new String(painterList);
	}
	
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {
		super.load(painting, section);
		this.notHolding = painting.getLocale(NOT_HOLDING, notHolding, section);
		this.name = painting.getLocale(NAME, name, section);
		this.owner = painting.getLocale(OWNER, owner, section);
		this.painter = painting.getLocale(PAINTER, painter, section);
		this.privilege = painting.getLocale(PREVILEGE, privilege, section);
		this.painterListitem = painting.getLocale(PAINTER_LISTITEM, painterListitem, section);
	}
}