package net.aegistudio.mpp.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import net.aegistudio.mpp.MapPainting;

/**
 * Specially inteded for CraftBukkit servers, which allow you to
 * send packets or generate more concrete clazz.
 * 
 * @author aegistudio
 */

public class CraftPacketSender implements PacketSender {
	public final Method getHandleMethod;
	
	public String playerConnection = "playerConnection";
	public Field playerConnectionField;
	
	public Method sendPacketMethod;
	
	public final String craftbukkitPackage;
	public final String minecraftPackage;
	
	public CraftPacketSender(MapPainting painting) throws Exception {
		// Retrieve craft bukkit package.
		this.craftbukkitPackage = painting.getServer().getClass().getPackage().getName();
		
		// Retrieve minecraft package.
		Field minecraftServerField = Class.forName(craftbukkitPackage + ".CraftServer").getDeclaredField("console");
		minecraftServerField.setAccessible(true);
		Object minecraftServer = minecraftServerField.get(painting.getServer());
		this.minecraftPackage = minecraftServer.getClass().getPackage().getName();
		minecraftServerField.setAccessible(false);
		
		// Retrieve player connection.
		getHandleMethod = Class.forName(craftbukkitPackage + ".entity.CraftPlayer").getMethod("getHandle");
		playerConnectionField = Class.forName(minecraftPackage + ".EntityPlayer").getDeclaredField("playerConnection");
		for(Method m : Class.forName(minecraftPackage + ".PlayerConnection").getDeclaredMethods())
			if(m.getName().equals("sendPacket")) {	sendPacketMethod = m; break; }
		if(sendPacketMethod == null) throw new NoSuchFieldException("sendPacket");
	}
	
	public Object getHandle(Player player) {
		try {
			return getHandleMethod.invoke(player);
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public <Packet> void sendPacket(Player player, Packet packet) {
		try {
			Object connection = playerConnectionField.get(getHandle(player));
			sendPacketMethod.invoke(connection, packet);
		}
		catch(Exception e) {
			
		}
	}
}
