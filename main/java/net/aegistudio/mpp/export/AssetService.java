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
 * <p>To retrieve the asset service, you should code like this:</p>
 * <p><b><code style="font-family: Courier New;">
 * &nbsp;&nbsp;//Retrieve asset service provider.<br>
 * &nbsp;&nbsp;AssetService service = plugin.getServer().getServicesManager()<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.getRegistration(AssetService.class).getProvider();<br>
 * </code></b></p>
 * 
 * <p>You could specify an asset by <b><code>[&lt;group&gt;/]&lt;name&gt;</code></b>,
 * like <b><code>string.left</code></b> refers to the algorithm named <b>left</b> under
 * the asset group <b>string</b>. And there could be more than two levels (say <b>a.b.c</b>)
 * if you really need. </p>
 * 
 * <p>The group node need to exist or be created before registering any asset under it.
 * You could create a group node by calling <code>group</code> method. The registering and retrieving
 * of an asset node are also quite easy, by calling <code>put</code> and <code>get</code> method.</p>
 * 
 * @see net.aegistudio.mpp.algo.LineGenerator
 * @see net.aegistudio.mpp.algo.FillGenerator
 * @see net.aegistudio.mpp.algo.CharacterGenerator
 * @see net.aegistudio.mpp.algo.StringGenerator
 * 
 * @author aegistudio
 */

public interface AssetService {
	/**
	 * Retrieve an asset.
	 * @param name the asset name. (Like <i>line</i> or <i>string.left</i>)
	 * @param clazz the actual class of this asset.
	 * @return the asset instance if exists.
	 */
	public <A extends Asset> A get(String name, Class<A> clazz);
	
	/**
	 * Register an asset.
	 * @param name the asset name. (Like <i>line</i> or <i>string.left</i>)
	 * @param asset the asset instance.
	 * @return the replaced asset.
	 */
	public <A extends Asset> Asset put(String name, A asset);
	
	/**
	 * Create an asset group.
	 * @param name the asset group name (Like <i>string</i> is the asset group of <i>string.left</i>)
	 */
	public void group(String name);
}
