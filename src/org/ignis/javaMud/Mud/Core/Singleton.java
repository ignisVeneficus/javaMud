package org.ignis.javaMud.Mud.Core;

/**
 * Olyan osztalyok gyujtoje, amikbol csak egy lehet:
 * Room, Player
 * @author Ignis
 *
 */
public interface Singleton {

	/**
	 * Objectben van
	 * @return
	 * @see Object#_destrObject()
	 */
	boolean _destrObject();

	/**
	 * Objectben van
	 * @return
	 * @see Object#getStatus()
	 */
	int getStatus();

	

}
