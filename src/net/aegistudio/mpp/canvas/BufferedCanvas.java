package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.InteractInfo;
import net.aegistudio.mpp.MapPainting;

public class BufferedCanvas extends MapRenderer implements Canvas {
	public MapPainting painting;
	public byte[][] pixel;
	public int size;
	public final TreeSet<Integer> viewed 
		= new TreeSet<Integer>();
	
	public BufferedCanvas clone() {
		BufferedCanvas canvas = new BufferedCanvas();
		canvas.painting = painting;
		canvas.size = this.size;
		canvas.pixel = new byte[size][size];
		for(int i = 0; i < size; i ++)
			System.arraycopy(this.pixel[i], 0, canvas.pixel[i], 0, size);
		return canvas;
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(!viewed.contains(player.getEntityId())) {
			for(int i = 0; i < 128; i ++)
				for(int j = 0; j < 128; j ++) {
					canvas.setPixel(i, j, pixel
							[(int)(1.0 * i / 128 * size)]
							[(int)(1.0 * j / 128 * size)]);
				}
			viewed.add(player.getEntityId());
		}
	}
	
	@Override
	public void paint(InteractInfo interact, Color color) {
		int x = interact.x;
		if(x >= size || x < 0) return;
		int y = size - 1 - interact.y;
		if(y >= size || y < 0) return;
		
		if(color == null) pixel[x][y] = 0;
		else pixel[x][y] = (byte) painting.canvas.color.getIndex(color);
		
		viewed.clear();
	}
	
	@Override
	public Color look(int x, int y) {
		if(x >= size || x < 0) return null;
		y = size - 1 - y;
		if(y >= size || y < 0) return null;
		
		if(pixel[x][y] == 0) return null;
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
	public void load(MapPainting painting, InputStream file) throws Exception {
		DataInputStream din = new DataInputStream(file);
		this.size = din.readShort();
		
		this.pixel = new byte[size][size];
		this.painting = painting;
		
		GZIPInputStream input = new GZIPInputStream(file);
		for(int i = 0; i < size; i ++)
			for(int j = 0; j < size; j ++) {
				int next = input.read();
				if(next == -1) break;
				this.pixel[i][j] = (byte) next;
			}
	}

	@Override
	public void save(MapPainting painting, OutputStream file) throws Exception {
		DataOutputStream dout = new DataOutputStream(file);
		dout.writeShort(size);
		
		GZIPOutputStream output = new GZIPOutputStream(file);
		for(int i = 0; i < size; i ++) 
			output.write(this.pixel[i], 0, size);
		
		output.finish();
		output.flush();
	}

	@Override
	public boolean interact(InteractInfo info) {
		return false;
	}
}
