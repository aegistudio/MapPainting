package net.aegistudio.mpp.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.World;

public class WorldAccess {
	Field worldField;
	Method getTileEntityMethod;
	Method setTileEntityMethod;
	
	Constructor<?> playOutBlockUpdate;
	public WorldAccess(String craftbukkitPackage, String minecraftPackage) throws Exception {
		Class<?> craftworld = Class.forName(craftbukkitPackage + ".CraftWorld");
		worldField = craftworld.getDeclaredField("world");
		
		Class<?> world = Class.forName(minecraftPackage + ".World");
		getTileEntityMethod = forMethod(world, "getTileEntity", 1);
		setTileEntityMethod = forMethod(world, "setTileEntity", 2);
		
		Class<?> poBlockChange = Class.forName(minecraftPackage + ".PacketPlayOutBlockChange");
		for(Constructor<?> con : poBlockChange.getConstructors()) 
			if(con.getParameterCount() == 2) {
				playOutBlockUpdate = con;
				break;
			}
		if(playOutBlockUpdate == null) throw new NoSuchMethodException();
	}
	
	public Method forMethod(Class<?> clazz, String name, int arglen) throws NoSuchMethodException {
		for(Method method : clazz.getDeclaredMethods()) 
			if(method.getName().equals(name) && 
					method.getParameterCount() == arglen) return method;
		throw new NoSuchMethodException(name);
	}
	
	public Object getWorld(World world) {
		try {
			worldField.setAccessible(true);
			return worldField.get(world);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			worldField.setAccessible(false);
		}
	}
	
	public <BlockPosition> Object getTileEntity(World world, BlockPosition blockPosition) {
		try {
			return getTileEntityMethod.invoke(getWorld(world), blockPosition);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public <BlockPosition, TileEntity> void setTileEntity(World world, BlockPosition blockPosition, TileEntity tileEntity) {
		try {
			setTileEntityMethod.invoke(getWorld(world), blockPosition, tileEntity);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public <BlockPosition> Object updateBlock(World world, BlockPosition blockPosition) {
		try {
			return playOutBlockUpdate.newInstance(getWorld(world), blockPosition);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
