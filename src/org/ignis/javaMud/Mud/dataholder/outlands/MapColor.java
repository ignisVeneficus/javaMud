package org.ignis.javaMud.Mud.dataholder.outlands;

import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.dataholder.RoomItem;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.utils.xml.StringStringColorAdapter;
/**
 * 
 * @author Ignis
 * Egy adott szin informacioit tartalmazza
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MapColor {
	static private Logger LOG = LogManager.getLogger(MapColor.class);
	
	/**
	 * tipusa, szoba, vagy csak van
	 */
	@XmlAttribute
	private String type;
	/**
	 * terkepeszeti tipusa, terkepnel van jelentosege
	 */
	@XmlAttribute
	private String mapType;
	/**
	 * terkepeszeti altipus, pl Folyo: patak/er/folyo/folyam.. stb
	 * vagy az ut tipusa, oszveny/foldut/stb
	 */
	@XmlAttribute
	private String mapSubType;
	/**
	 * Viz-e, vayis uszik-e benne az illeto
	 */
	@XmlAttribute
	private boolean isWater;
	/**
	 * szin kodja, hexaban (rgb)
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(StringStringColorAdapter.class)
	private StringColor rgb;
	
	/**
	 * Tavozasnal Az SP lost szorzo
	 */
	@XmlAttribute
	private int SPLost;
	
	
	/**
	 * kiirando erzesek
	 */
	@XmlElement(name="stimulus")
	private ArrayList<Stimulus> stimuluses;
	/**
	 * nezheto elemek
	 * 
	 */
	@XmlElement(name="item")
	private ArrayList<RoomItem> items;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMapType() {
		return mapType;
	}
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}
	public boolean isWater() {
		return isWater;
	}
	public void setWater(boolean isWater) {
		this.isWater = isWater;
	}
	public StringColor getRgb() {
		return rgb;
	}
	public void setRgb(StringColor rgb) {
		this.rgb = rgb;
	}
	public ArrayList<Stimulus> getStimuluses() {
		return stimuluses;
	}
	public void setStimuluses(ArrayList<Stimulus> stimuluses) {
		this.stimuluses = stimuluses;
	}
	public MapColor() {
		stimuluses = new ArrayList<>();
		items = new ArrayList<>();
	}
	public String getMapSubType() {
		return mapSubType;
	}
	public void setMapSubType(String mapSubType) {
		this.mapSubType = mapSubType;
	}
	public String getMapFullType() {
		return (StringUtils.isBlank(mapType)?"":mapType) + "/" + (StringUtils.isBlank(mapSubType)?"":mapSubType);
	}
	public int getSPLost() {
		return SPLost;
	}
	public void setSPLost(int sPLost) {
		SPLost = sPLost;
	}
	public ArrayList<RoomItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<RoomItem> items) {
		this.items = items;
	}
	void afterUnmarshal(Unmarshaller unmarshaller, java.lang.Object parent) {
		LOG.trace("After afterUnmarshal: " + getRgb());
		for(Stimulus s:stimuluses) {
			LOG.trace(s.toString());
		}
	}
}
