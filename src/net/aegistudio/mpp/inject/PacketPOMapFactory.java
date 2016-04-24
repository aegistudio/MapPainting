package net.aegistudio.mpp.inject;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.canvas.Graphic;

/**
 * This class is responsible for generating
 * an minecraft packet instance of PacketPlayOutMap.
 * 
 * @author aegistudio
 */

public class PacketPOMapFactory {
	public Constructor<?> constructor;
	public EnumPlayOutMap newPacket;
	public Class<?> packetClass;
	
	public PacketPOMapFactory(String minecraftPackage) throws Exception {
		if(newPacket == null) {
			Class<?> packetClass = Class.forName(minecraftPackage + ".PacketPlayOutMap");
			for(Constructor<?> constructor : packetClass.getConstructors()) {
				this.newPacket = EnumPlayOutMap.get(constructor);
				this.constructor = constructor;
				if(newPacket != null) break;
			}
			if(newPacket == null) return;
		}
	}
	
	public Object newMapPacket(Player player, MapView view, Graphic graphic) {
		try {
			constructor.setAccessible(true);
			return newPacket.createPacket(constructor, view, graphic.pixel, 0,
					graphic.rowMin, 128, graphic.rowMax - graphic.rowMin + 1);
		}
		catch(Exception e) {
			return null;
		}
		finally {
			constructor.setAccessible(false);
		}
	}
}
