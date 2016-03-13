package net.aegistudio.mpp.palette;

import java.awt.Color;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

import net.aegistudio.mpp.MapPainting;

public class DyeItem {
	public MapPainting painting;
	public DyeItem(MapPainting painting) {
		this.painting = painting;
	}
	
	@SuppressWarnings("deprecation")
	public Color getColor(ItemStack dye) {
		java.awt.Color awtColor = painting.palette.getColor(dye);
		if(awtColor != null) return awtColor;
		org.bukkit.Color color = DyeColor.getByDyeData((byte) dye.getDurability()).getColor();
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	@SuppressWarnings("deprecation")
	public void setColor(ItemStack dye, Color newColor) {
		painting.palette.setColor(dye, newColor);
		int distance = Integer.MAX_VALUE;
		short damage = 0;
		for(DyeColor color : DyeColor.values()) {
			int redDistance = color.getColor().getRed() - newColor.getRed();
			int greenDistance = color.getColor().getGreen() - newColor.getGreen();
			int blueDistance = color.getColor().getBlue() - newColor.getBlue();
			int newDistance = redDistance * redDistance + greenDistance * greenDistance 
					+ blueDistance * blueDistance;
			if(newDistance < distance) {
				distance = newDistance;
				damage = color.getDyeData();
			}
		}
		dye.setDurability(damage);
	}
}
