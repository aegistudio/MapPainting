package net.aegistudio.mpp.canvas;

import org.bukkit.command.CommandSender;

public enum EnumCanvasFactory implements CanvasFactory {
	NORMAL(BufferedCanvas.class){
		@Override
		public Canvas create() {
			return new BufferedCanvas();
		}

		@Override
		public Canvas create(CommandSender sender, String[] arguments) {
			BufferedCanvas canvas = new BufferedCanvas();
			canvas.pixel = new byte[canvas.size][canvas.size];
			for(int i = 0; i < canvas.size; i ++)
				for(int j = 0; j < canvas.size; j ++)
					canvas.pixel[i][j] = (byte)0x22;
			return canvas;
		}
	};
	
	public final Class<? extends Canvas> substance;
	
	private EnumCanvasFactory(Class<? extends Canvas> substance) {
		this.substance = substance;
	}
	
	public static CanvasFactory getFactory(String factory) {
		try {
			return valueOf(factory.toUpperCase());
		}
		catch(Throwable t) {
			try {
				return (CanvasFactory)(Class.forName(factory).newInstance());
			}
			catch(Throwable e) {
				return null;
			}
		}
	}
	
	public static String getFactory(Canvas input) {
		for(EnumCanvasFactory factory : values()) {
			if(factory.substance.equals(input.getClass()))
				return factory.name().toLowerCase();
		}
		return input.getClass().getName();
	}
}
