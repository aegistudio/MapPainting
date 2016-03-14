package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.MapPainting;

public class BufferedCanvas extends MapRenderer implements Canvas {
	public MapPainting painting;
	public byte[][] pixel;
	public int size = 128;
	public final TreeSet<Integer> viewed 
		= new TreeSet<Integer>();
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(!viewed.contains(player.getEntityId())) {
			for(int i = 0; i < size; i ++)
				for(int j = 0; j < size; j ++)
					canvas.setPixel(i, j, pixel[i][j]);
			viewed.add(player.getEntityId());
		}
	}
	
	@Override
	public void paint(int x, int y, Color color) {
		if(x >= size || x < 0) return;
		y = size - y;
		if(y >= size || y < 0) return;
		
		pixel[x][y] = (byte) painting.canvas.color.getIndex(color);
		viewed.clear();
	}
	
	@Override
	public Color look(int x, int y) {
		return painting.canvas.color.getColor(pixel[x][y]);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public MapRenderer getRenderer() {
		return this;
	}
	
	@Override
	public void load(MapPainting painting, MapCanvasRegistry registry, 
			ConfigurationSection config) throws Exception {
		this.pixel = new byte[size][size];
		this.painting = painting;
		
		File file = new File(painting.getDataFolder(), registry.name.concat(".mpp"));
		if(!file.exists()) file.createNewFile();
		else {
			GZIPInputStream input 
				= new GZIPInputStream(new FileInputStream(file));
			for(int i = 0; i < size; i ++)
				for(int j = 0; j < size; j ++) {
					int next = input.read();
					if(next == -1) break;
					this.pixel[i][j] = (byte) next;
				}
			input.close();
		}
	}

	@Override
	public void save(MapPainting painting, MapCanvasRegistry registry, 
			ConfigurationSection config) throws Exception {
		File file = new File(painting.getDataFolder(), registry.name.concat(".mpp"));
		if(!file.exists()) file.createNewFile();
		
		GZIPOutputStream output
			= new GZIPOutputStream(new FileOutputStream(file));
		for(int i = 0; i < size; i ++) 
			output.write(this.pixel[i], 0, size);
		
		output.finish();
		output.flush();
		
		output.close();
	}

	@Override
	public boolean interact(int x, int y, Player player) {
		return false;
	}
}
