package org.ignis.javaMud.Mud.Core;
/**
 * Interface, ami a heartbeat-et teszi lehetove, vagyis adott idokozonkent meghivasra kerul a tick() fuggveny 
 * @author Ignis
 *
 */
public interface HeartBeatListener {
	/**
	 * A heartbeat kezelo hivja adott idokozonkent
	 */
	public void tick();
	/**
	 * logolasi okai vannak, elvileg mindenkinek az mud Object leszarmazottjanak kell lennie, ott pedig definialva van
	 * @return
	 */
	public String getFullObjectName();
}
