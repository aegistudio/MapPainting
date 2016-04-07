package net.aegistudio.mpp.export;

/**
 * <p>Manage assets like algorithms, fonts/bitmaps, pictures, etc.</p>
 * 
 * <p>Please notice that any canvases can retrieve assets from the asset class, 
 * which means that assets are <b>shared</b>. (So there means you may be sharing
 * variables by asset manager, though it is deprecated.)</p>
 * 
 * <p>By default configuration, we have embedded these assets in Map Painting,
 * you could add your assets if you want whatever.
 * <li><b>line</b>: a line generator, draw a line.</li>
 * <li><b>fill</b>: a fill generator, fill a enclosed region.</li>
 * <li><b>char</b>: a character generator, draw a character of <i>Minecraft Font</i>.</li>
 * <li><b>string.left</b>: a string generator, draw a left-aligned string of <i>Minecraft Font</i>.</li>
 * <li><b>string.center</b>: a string generator, draw a center-aligned string of <i>Minecraft Font</i>.</li>
 * <li><b>string.right</b>: a string generator, draw a right-aligned string of <i>Minecraft Font</i>.</li>
 * </p>
 * 
 * @see net.aegistudio.mpp.algo.LineGenerator
 * @see net.aegistudio.mpp.algo.FillGenerator
 * @see net.aegistudio.mpp.algo.CharacterGenerator
 * @see net.aegistudio.mpp.algo.StringGenerator
 * 
 * @author aegistudio
 */

public interface AssetService {
	public <A extends Asset> A get(String name, Class<A> clazz);
	
	public <A extends Asset> Asset put(String name, A asset);
	
	public void group(String name);
}
