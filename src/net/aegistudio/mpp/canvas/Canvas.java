package net.aegistudio.mpp.canvas;

import java.awt.Color;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import net.aegistudio.mpp.MapPainting;

public interface Canvas {
	public void load(MapPainting painting, MapCanvasRegistry registry, ConfigurationSection config) throws Exception;
	
	public void paint(int x, int y, Color color);
	
	public Color look(int x, int y);
	
	public boolean interact(int x, int y, Player player);
	
	public MapRenderer getRenderer();
	
	public int size();
	
	public void save(MapPainting painting, MapCanvasRegistry registry, ConfigurationSection config) throws Exception;
}