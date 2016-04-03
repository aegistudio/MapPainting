package net.aegistudio.mpp.intrude;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import net.aegistudio.mpp.canvas.Graphic;

public class CraftPacketSender implements PacketSender {
	public String getHandle = "getHandle";
	public Method getHandleMethod;
	
	public String playerConnection = "playerConnection";
	public Field playerConnectionField;
	
	public String packetClazz = "PacketPlayOutMap";
	public Class<?> packetClass;
	
	public String sendPacket = "sendPacket";
	public Method sendPacketMethod;
	
	public Constructor<?> constructor;
	public EnumPlayOutMap newPacket;

	@Override
	public void sendPacket(Player player, MapView view, Graphic graphic){
		if(!graphic.dirty) return;
		try {
			if(getHandleMethod == null) getHandleMethod = player.getClass().getMethod(this.getHandle);
			Object handle = getHandleMethod.invoke(player);
			
			if(playerConnectionField == null)
				playerConnectionField = handle.getClass().getField(this.playerConnection);
			Object connection = playerConnectionField.get(handle);
			
			if(sendPacketMethod == null) {
				for(Method m : connection.getClass().getDeclaredMethods())
					if(m.getName().equals(this.sendPacket)){
						sendPacketMethod = m;
						break;
					}
				if(sendPacketMethod == null) return;
			}
			
			if(newPacket == null) {
				Class<?> packetClass = Class.forName(connection.getClass().getPackage().getName() + "." + this.packetClazz);
				for(Constructor<?> constructor : packetClass.getConstructors()) {
					this.newPacket = EnumPlayOutMap.get(constructor);
					this.constructor = constructor;
					if(newPacket != null) break;
				}
				if(newPacket == null) return;
			}
			
			constructor.setAccessible(true);
			Object packet = newPacket.createPacket(constructor, view, graphic.pixel, 0,
					graphic.rowMin, 128, graphic.rowMax - graphic.rowMin + 1);
			
			constructor.setAccessible(false);
			sendPacketMethod.invoke(connection, packet);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
