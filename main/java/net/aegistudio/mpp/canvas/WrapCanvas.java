package net.aegistudio.mpp.canvas;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import net.aegistudio.mpp.Interaction;
import net.aegistudio.mpp.MapPainting;
import net.aegistudio.mpp.algo.Paintable;
import org.bukkit.map.MapView;

public class WrapCanvas extends Canvas implements CanvasWrapper {
	private String wrapping = "";
	private int currentCount = -1;
	private MapCanvasRegistry wrappedCanvas;

	public WrapCanvas(MapPainting painting) {
		super(painting);
	}

	@Override
	public void load(MapPainting painting, InputStream input) throws Exception {
		DataInputStream din = new DataInputStream(input);
		this.setWrapping(0, din.readUTF());
	}

	@Override
	public void save(MapPainting painting, OutputStream output) throws Exception {
		DataOutputStream dout = new DataOutputStream(output);
		dout.writeUTF(this.wrapping);
	}

	@Override
	public void paint(Interaction interact, Color color) {
		this.retrieve();
		if (this.wrappedCanvas == null) {
			return;
		}
		if (interact.sender != null && !this.wrappedCanvas.canPaint(interact.sender)) {
			return;
		}
		this.wrappedCanvas.canvas.paint(interact, color);
	}

	@Override
	public Color look(int x, int y) {
		this.retrieve();
		if (this.wrappedCanvas == null) {
			return null;
		}
		return this.wrappedCanvas.canvas.look(x, y);
	}

	@Override
	public boolean interact(Interaction interact) {
		this.retrieve();
		if (this.wrappedCanvas == null) {
			return false;
		}
		if (!interact.sender.hasPermission("mpp.interact")) {
			return false;
		}
		return this.wrappedCanvas.canvas.interact(interact);
	}

	@Override
	public int size() {
		this.retrieve();
		if (this.wrappedCanvas == null) {
			return 0;
		}
		return this.wrappedCanvas.canvas.size();
	}

	@Override
	public void tick() {
		this.retrieve();
		if (this.wrappedCanvas != null && !this.wrappedCanvas.canvas.hasObserver(this)) {
			this.repaint();
		}
		super.tick();
	}

	@Override
	public WrapCanvas clone() {
		WrapCanvas newCanvas = new WrapCanvas(this.painting);
		this.copy(newCanvas);
		return newCanvas;
	}

	protected void copy(WrapCanvas another) {
		another.wrapping = this.wrapping;
		another.wrappedCanvas = this.wrappedCanvas;
		another.currentCount = this.currentCount;
	}

	public void retrieve() {
		if (this.currentCount < this.painting.m_canvasManager.count && this.wrappedCanvas == null) {
			this.wrappedCanvas = this.painting.m_canvasManager.nameCanvasMap.get(this.wrapping);
			if (this.wrappedCanvas != null) {
				this.repaint();
			}
		}
		if (this.currentCount > this.painting.m_canvasManager.count && this.wrappedCanvas != null
				&& this.wrappedCanvas.removed()) {
			this.wrappedCanvas = null;
			this.repaint();
		}
		this.currentCount = this.painting.m_canvasManager.count;
	}

	@Override
	public void setWrapping(int layer, String newWrapping) {
		if (this.wrapping != null && this.wrapping.equals(newWrapping)) {
			return;
		}
		this.wrapping = newWrapping;
		this.currentCount = -1;
		this.wrappedCanvas = null;
		this.retrieve();
	}

	@Override
	protected void subrender(MapView view, Paintable canvas) {
		if (this.wrappedCanvas == null) {
			return;
		}
		this.wrappedCanvas.canvas.subrender(view, canvas);
	}

	@Override
	public void showWrapped(Set<String> container) {
		container.add(this.wrapping);
		this.retrieve();
		if (this.wrappedCanvas != null && !this.wrappedCanvas.removed()
				&& this.wrappedCanvas.canvas instanceof CanvasWrapper) {
			((CanvasWrapper) ((Object) this.wrappedCanvas.canvas)).showWrapped(container);
		}
	}
}
