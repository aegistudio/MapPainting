package net.aegistudio.mpp.inject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PacketPOSignFactory {
	public Constructor<?> blockLocation;
	public Constructor<?> tileEntitySign;
	public Method setOwner;
	
	public Constructor<?> newPoSignEditorPacket;
	public Class<?> openSignEditorClass;
	
	public Constructor<?> chatComponentText;
	
	public Method getUpdatePacket;
	public Field lines;
	
	public PacketPOSignFactory(String minecraftPackage) throws Exception {
		blockLocation = Class.forName(minecraftPackage + ".BlockPosition")
			.getDeclaredConstructor(int.class, int.class, int.class);
		
		Class<?> tileEntitySignClazz = Class.forName(minecraftPackage + ".TileEntitySign");
		tileEntitySign = tileEntitySignClazz.getDeclaredConstructor();
		
		Class<?> entityHuman = Class.forName(minecraftPackage + ".EntityHuman");
		for(Method m : tileEntitySignClazz.getDeclaredMethods()) {
			if(m.getReturnType() == void.class && m.getParameterCount() == 1)
				if(m.getParameters()[0].getType() == entityHuman) {
					this.setOwner = m;
					break;
				}
		}
		if(setOwner == null) throw new NoSuchMethodException();
		
		Class<?> packetClass = Class.forName(minecraftPackage + ".PacketPlayOutOpenSignEditor");
		for(Constructor<?> constructor : packetClass.getConstructors()) 
			if(constructor.getParameterCount() == 1) {
				newPoSignEditorPacket = constructor;
				break;
			}
		if(newPoSignEditorPacket == null) throw new NoSuchMethodException();
		
		chatComponentText = Class.forName(minecraftPackage + ".ChatComponentText").getConstructor(String.class);
		getUpdatePacket = tileEntitySignClazz.getMethod("getUpdatePacket");
		lines = tileEntitySignClazz.getField("lines");
	}
	
	public Object newBlockLocation(int x, int y, int z) throws Exception{
		return blockLocation.newInstance(x, y, z);
	}
	
	public Object newTileEntitySign(Object owner) throws Exception {
		Object sign = tileEntitySign.newInstance();
		this.setOwner.invoke(sign, owner);
		return sign;
	}
	
	public Object newSignEditPacket(Object blockLocation) {
		try {
			newPoSignEditorPacket.setAccessible(true);
			return newPoSignEditorPacket.newInstance(blockLocation);
		}
		catch(Exception e) {
			return null;
		}
		finally {
			newPoSignEditorPacket.setAccessible(false);
		}
	}
	
	public void setText(Object tileEntitySign, int ln, String input) {
		try {
			Object instance = chatComponentText.newInstance(input);
			Array.set(lines.get(tileEntitySign), ln, instance);
		}
		catch(Exception e) {
		}
	}
	
	public Object newSignUpdatePacket(Object tileEntitySign) {
		try {
			return getUpdatePacket.invoke(tileEntitySign);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
