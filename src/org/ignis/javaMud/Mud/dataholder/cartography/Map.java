package org.ignis.javaMud.Mud.dataholder.cartography;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Terkep megjelenitese
 * Teljesen fuggetlen a tenyleges kapcsoldasoktol, vagyis lehetnek olyan szobaj, iranyok, stb, amik a terkeprol hianyoznak
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="Map")
public class Map {
	/**
	 * Szobak: lokaciok
	 */
	@XmlElement(name="loc")
	private ArrayList<MapItem> items;
	/**
	 * Kapcsolatok
	 */
	@XmlElement(name="con")
	private ArrayList<MapLine> lines;
	public Map() {
		items = new ArrayList<>();
		lines = new ArrayList<>();
	}
	public ArrayList<MapItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<MapItem> items) {
		this.items = items;
	}
	public ArrayList<MapLine> getLines() {
		return lines;
	}
	public void setLines(ArrayList<MapLine> lines) {
		this.lines = lines;
	}
}
