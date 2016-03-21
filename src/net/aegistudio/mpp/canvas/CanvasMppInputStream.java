package net.aegistudio.mpp.canvas;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.aegistudio.mpp.MapPainting;

public class CanvasMppInputStream extends InputStream implements AutoCloseable {

	private final InputStream inputStream;
	public CanvasMppInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public Canvas readCanvas(MapPainting painting) throws Exception {
		DataInputStream din = new DataInputStream(inputStream);
		
		if(din.readByte() != 'P') throw new Exception("Wrecked file!");
		if(din.readByte() != 'P') throw new Exception("Wrecked file!");
		if(din.readByte() != 'M') throw new Exception("Wrecked file!");
		
		String canvasClass = din.readUTF();
		Canvas canvas = (Canvas) Class.forName(canvasClass)
				.getConstructor(MapPainting.class).newInstance(painting);
		canvas.load(painting, this.inputStream);
		
		return canvas;
	}
	
	@Override
	public int read() throws IOException {
		return this.inputStream.read();
	}

	public void close() throws IOException {
		this.inputStream.close();
	}
}
