

package net.aegistudio.mpp.factory;

import net.aegistudio.mpp.ActualHandle;
import net.aegistudio.mpp.HazardCommand;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.canvas.Canvas;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

public abstract class ConcreteCreateSubCommand extends ActualHandle implements HazardCommand {
  protected String paramList = "[<parameter>]";
  
  public boolean handle(MapPainting painting, String prefix, CommandSender sender, String[] arguments) {
    if (arguments.length < 1) {
      sender.sendMessage(prefix + " <name> " + this.paramList);
    } else {
      if (!(sender instanceof Player)) {
        sender.sendMessage(painting.m_commandCreateHandler.onlyPlayer);
        return true;
      } 
      Player player = (Player)sender;
      String name = arguments[0];
      
      // sanitise newname
      name = name.replace('.', '_');
      name = name.replace('/', '_');
      name = name.replace('\\', '_');
      
      if (painting.m_canvasManager.nameCanvasMap.containsKey(name)) {
        sender.sendMessage(painting.m_commandCreateHandler.canvasAlreadyExisted.replace("$canvasName", name));
        return true;
      } 
      String[] subArguments = new String[arguments.length - 1];
      System.arraycopy(arguments, 1, subArguments, 0, arguments.length - 1);
      Canvas canvasInstance = create(painting, (CommandSender)player, subArguments);
      if (canvasInstance != null) {
        MapCanvasRegistry canvas = new MapCanvasRegistry(name);
        canvas.canvas = canvasInstance;
        canvas.owner = player.getName();
        canvas.painter.add(player.getName());
        
          if (painting.m_commandCreateHandler.promptConfirm) {
            sender.sendMessage(painting.m_commandCreateHandler.notHoldingMap);
            painting.m_commandConfirmHandler.hazard(sender, this, canvas);
          } else {
            handle(painting, sender, canvas);
          } 
          return true;

      } 
    } 
    return true;
  }
  
  protected void commit(MapPainting plugin, CommandSender sender, MapCanvasRegistry canvas) {
    plugin.m_canvasManager.add(canvas);
    if (sender instanceof Player) {
      Player player = (Player)sender;
      plugin.m_canvasManager.give(player, canvas, 1);
    } 
    sender.sendMessage(plugin.m_commandCreateHandler.bound.replace("$canvasName", canvas.name));
    plugin.ackHistory(canvas, sender);
  }
  
  protected abstract Canvas create(MapPainting paramMapPainting, CommandSender paramCommandSender, String[] paramArrayOfString);
  
  public void handle(MapPainting plugin, CommandSender sender, Object state) {
    
	  MapCanvasRegistry registry = (MapCanvasRegistry)state;
	  Server server = Bukkit.getServer();
	  MapView view = null;
	  
	  //ItemStack m = new ItemStack(Material.FILLED_MAP);
      //MapMeta meta = (MapMeta) m.getItemMeta();
      //view = meta.getMapView();
	  
	  
	  
	  // if pool is empty
	  if (plugin.m_canvasManager.pool.isEmpty()) {
		  	sender.sendMessage("CREATION - Pool is empty, defaulting to temporary view value?");
		  	World w = server.getWorlds().get(0);
		  		view = server.createMap(w);
      } else {
        	int i = plugin.m_canvasManager.pool.pollFirst();//.shortValue();
        	view = server.getMap(i);
        	sender.sendMessage("CREATION - Pool was not empty, view is from map " + i);
      }
	  
	  // pool is the pool of deleted maps, already in use.
	  
	// if view is null still something is very, very wrong - abort
	if (view == null) {
		sender.sendMessage("CREATION - View is null still. Bit of an issue here. Missing data files?");
		return;
	}

	  // DEBUG
	  sender.sendMessage("CREATION - Map ID retrieved was " + view.getId());

	  // OUT OF RANGE CHECK
	  if (view.getId() < 0) {
		  sender.sendMessage(plugin.m_commandCreateHandler.tooManyMap);
		  return;
	  }
    
	  // UPDATE THE REGISTRY WITH NEW BINDINGS
	  registry.binding = (int) view.getId();
	  registry.view = view;

	  // COMMIT THE CHANGES
	  commit(plugin, sender, registry);
	  
  }
}

