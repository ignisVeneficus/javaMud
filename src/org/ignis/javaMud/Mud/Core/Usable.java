package org.ignis.javaMud.Mud.Core;
/**
 * Olyan interface, mely a hasznalhato targyakat fogja ossze foleg a GUI, es a szornybetolto miatt
 * megfog, visel, stb minden egy fugvennyel erheto el
 * @author Ignis
 *
 */
public interface Usable {
	/**
	 * Megfog,visel, felvesz, stb, amikor a targy hasznalatba kerul
	 */
	public void use();
	/**
	 * eltesz, levesz, stb amikor a targy hasznalata veget er
	 */
	public void unUse();
	
	/**
	 * eltesz, levesz, stb amikor a targy hasznalata veget er
	 * Mar nem nez semmi ellenorzest, nem ir ki semmi uzenetet
	 */
	
	public void _unUse();
	
	/**
	 * Eppen hasznalatban van-e
	 * @return
	 */
	public boolean isUsed();
}
