package net.aegistudio.mpp;

import java.util.TreeMap;

/**
 * Asset manager is a class that manages assets. The common assets are
 * algorithms, fonts/bitmaps, pictures, etc.
 * 
 * Please notice that any canvases can retrieve assets from the asset class, 
 * which means that assets are shared. (So there means you may be sharing
 * variables by asset manager, though it is deprecated.)
 * 
 * @author aegistudio
 */

public class AssetManager extends TreeMap<String, Asset> implements Asset {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public <A extends Asset> A get(String name, Class<A> clazz) {
		if(name == null) return null;
		int dot = name.indexOf('.');
		if(dot < 0) return (A) super.get(name);
		return ((AssetManager)super.get(name.substring(0, dot)))
				.get(name.substring(dot + 1), clazz);
	}
	
	public Asset put(String name, Asset asset) {
		if(name == null) return null;
		int dot = name.indexOf('.');
		if(dot < 0) return super.put(name, asset);
		return ((AssetManager)super.get(name.substring(0, dot)))
				.put(name.substring(dot + 1), asset);
	}
	
	public void group(String name) {
		put(name, new AssetManager());
	}
}
