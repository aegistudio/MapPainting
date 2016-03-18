package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import net.aegistudio.mpp.MapPainting;

public interface Canvas extends Cloneable {
	public void load(MapPainting painting, MapCanvasRegistry registry, InputStream mppFile) throws Exception;
	
	public void paint(int x, int y, Color color);
	
	public Color look(int x, int y);
	
	public boolean interact(int x, int y, Player player);
	
	public MapRenderer getRenderer();
	
	public int size();
	
	public void save(MapPainting painting, MapCanvasRegistry registry, OutputStream mppFile) throws Exception;
	
	public Canvas clone();
}