package net.aegistudio.mpp.intrude;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.bukkit.map.MapView;

@SuppressWarnings({"deprecation", "rawtypes"})
public enum EnumPlayOutMap {
	IB$B {
		@Override
		public Object createPacket(Constructor<?> constructor, MapView mapView, byte[] raster, int columnOffset,
				int rowOffset, int columnLength, int rowLength) throws Exception {
			return constructor.newInstance(mapView.getId(), raster, mapView.getScale().getValue());
		}
	},
	IBLCB$IIII {
		@Override
		public Object createPacket(Constructor<?> constructor, MapView mapView, byte[] raster, 
				int columnOffset, int rowOffset, int columnLength, int rowLength) throws Exception{
			return constructor.newInstance((int)mapView.getId(), mapView.getScale().getValue(), new ArrayList(),
					raster, columnOffset, rowOffset, columnLength, rowLength);
		}
	},
	IBZLCB$IIII {
		@Override
		public Object createPacket(Constructor<?> constructor, MapView mapView, byte[] raster, 
				int columnOffset, int rowOffset, int columnLength, int rowLength) throws Exception{
			return constructor.newInstance((int)mapView.getId(), mapView.getScale().getValue(), false, 
					new ArrayList(), raster, columnOffset, rowOffset, columnLength, rowLength);
		}
	};
	
	public abstract Object createPacket(Constructor<?> constructor, MapView mapView, byte[] raster, 
			int columnOffset, int rowOffset, int columnLength, int rowLength) throws Exception;
	
	public static EnumPlayOutMap get(Constructor<?> constructor) {
		StringBuilder builder = new StringBuilder();
		for(Class<?> type : constructor.getParameterTypes()) {
			if(type == int.class) builder.append("I");
			else if(type == boolean.class) builder.append("Z");
			else if(type == byte.class) builder.append("B");
			else if(type == byte[].class) builder.append("B$");
			else if(type == java.util.Collection.class) builder.append("LC");
		}
		try { return valueOf(new String(builder)); } catch(Throwable t) { return null; }
	}
}
