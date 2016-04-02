package net.aegistudio.mpp.algo;

import java.util.TreeMap;

public class Algorithm extends TreeMap<String, Generator> implements Generator {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public <G extends Generator> G get(String name, Class<G> clazz) {
		if(name == null) return null;
		int dot = name.indexOf('.');
		if(dot < 0) return (G) super.get(name);
		return ((Algorithm)super.get(name.substring(0, dot)))
				.get(name.substring(dot + 1), clazz);
	}
	
	public Generator put(String name, Generator algorithm) {
		if(name == null) return null;
		int dot = name.indexOf('.');
		if(dot < 0) return super.put(name, algorithm);
		return ((Algorithm)super.get(name.substring(0, dot)))
				.put(name.substring(dot + 1), algorithm);
	}
	
	public void group(String name) {
		put(name, new Algorithm());
	}
}
