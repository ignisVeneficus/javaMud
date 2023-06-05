package org.ignis.javaMud.Mud.dataholder.cartography;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
/**
 * Adott terkep egy szobaja
 * 
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MapItem {
	/**
	 * Id/azonosito, a megjelenito ezt probalja a szobaval osszekapcsolni, mikor megmutatja, hogy hol van a jatekos
	 */
	@XmlAttribute
	private String id;
	/**
	 * Magassag
	 */
	@XmlAttribute
	private int height;
	@XmlAttribute
	private int x;
	@XmlAttribute
	private int y;
	@XmlElement(name="type")
	private HashSet<String> types;
	@XmlElement(name="line")
	private HashMap<String,String> lines;
	@XmlElement(name="overlay")
	private ArrayList<String> overlays;
	@XmlElement(name="descr")
	private String descr;
	@XmlAttribute
	private String group;
	public MapItem() {
		types = new HashSet<>();
		lines = new HashMap<>();
		overlays = new ArrayList<>();
	}
	public MapItem(String id, int x, int y, HashSet<String> types, String descr) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.types = types;
		this.descr = descr;
		lines= new HashMap<>();
		overlays = new ArrayList<>();
	}
	public void addLine(String name, String line) {
		lines.put(name,line);
	}
	public void addOverlay(String overlay) {
		if(overlay!=null)
		overlays.add(overlay);
	}
	public int getHeight() {
		return height;
	}
	public HashSet<String> getTypes() {
		return types;
	}
	public HashMap<String, String> getLines() {
		return lines;
	}
	public ArrayList<String> getOverlays() {
		return overlays;
	}
	public String getDescr() {
		return descr;
	}
	public String getId() {
		return id;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getGroup() {
		return group;
	}
	
}
