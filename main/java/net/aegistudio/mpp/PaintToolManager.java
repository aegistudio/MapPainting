package net.aegistudio.mpp;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import net.aegistudio.mpp.canvas.MapCanvasRegistry;

public class PaintToolManager implements PaintTool {
	public final TreeMap<String, PaintTool> toolMap = new TreeMap<String, PaintTool>();
	
	@Override
	public void load(MapPainting painting, ConfigurationSection section) throws Exception {		
		for(Entry<String, PaintTool> toolEntry : toolMap.entrySet()) {
			if(!section.contains(toolEntry.getKey()))
				section.createSection(toolEntry.getKey());
			ConfigurationSection toolConfig = 
					section.getConfigurationSection(toolEntry.getKey());
			
			toolEntry.getValue().load(painting, toolConfig);
		}
	}

	@Override
	public void save(MapPainting painting, ConfigurationSection section) throws Exception {	
		for(Entry<String, PaintTool> toolEntry : toolMap.entrySet()) {
			if(!section.contains(toolEntry.getKey()))
				section.createSection(toolEntry.getKey());
			ConfigurationSection toolConfig = 
					section.getConfigurationSection(toolEntry.getKey());
			
			toolEntry.getValue().save(painting, toolConfig);
		}
	}

	@Override
	public boolean paint(ItemStack itemStack, MapCanvasRegistry canvas, Interaction interact) {
		for(PaintTool tool : toolMap.values()) 
			if(tool.paint(itemStack, canvas, interact)) 
				return true;
		return false;
	}
}
