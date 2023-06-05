package org.ignis.javaMud.Mud.dataholder;

import java.util.List;
import java.util.Map;


/**
 * Szoba leiras, ezt epiti fel a szoba, es adja oda a jatekos objektumnak
 * @author Ignis
 *
 */
public class RoomDescription {
	/**
	 * Erzekszervi leirasok
	 */
	private Map<String,String> senseDescription;
	/**
	 * Eszlelt kijaratok
	 */
	private List<String> exits;
	/**
	 * Eszlelt targyak
	 */
	private List<String> objects;
	/**
	 * Eszlelt elolenyek
	 */
	private List<String> livings;
	public RoomDescription(Map<String, String> senseDescription, List<String> exits, List<String> items,
			List<String> livings2) {
		super();
		this.senseDescription = senseDescription;
		this.exits = exits;
		this.objects = items;
		this.livings = livings2;
	}
	public String getSenseDescription(String type) {
		return senseDescription.get(type);
	}
	public List<String> getExits() {
		return exits;
	}
	public List<String> getObjects() {
		return objects;
	}
	public List<String> getLivings() {
		return livings;
	}
	
}
