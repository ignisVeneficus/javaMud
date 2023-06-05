package org.ignis.javaMud.Mud.dataholder.outlands;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.dataholder.Exit;
import org.ignis.javaMud.Mud.dataholder.ObjectRef;
import org.ignis.javaMud.Mud.dataholder.RoomItem;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
/**
 * Kapcsolatok az outlands es a kulonbozo teruletek kozott
 * 
 * @author Csaba Toth
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MapAddon {
	/**
	 * outlands X koordinata
	 */
	@XmlAttribute
	private int x;
	/**
	 * outlands Y koordinata
	 */
	@XmlAttribute
	private int y;

	@XmlAttribute(name="displayType")
	private String type;

	/**
	 * kiirando erzesek
	 */
	@XmlElement(name="stimulus")
	private ArrayList<Stimulus> stimuluses;

	
	/**
	 * extra kijaratai
	 */
	@XmlElement(name="exit")
	private ArrayList<Exit> exits;
	
	@XmlElement(name="item")
	private ArrayList<RoomItem> items;

	@XmlElement(name="objRef")
	private ArrayList<ObjectRef> objRefs;
	

	
	public ArrayList<Stimulus> getStimuluses() {
		return stimuluses;
	}

	public void setStimuluses(ArrayList<Stimulus> stimuluses) {
		this.stimuluses = stimuluses;
	}

	public ArrayList<Exit> getExits() {
		return exits;
	}

	public void setExits(ArrayList<Exit> exits) {
		this.exits = exits;
	}
	
	public MapAddon() {
		stimuluses =  new ArrayList<>();
		exits = new ArrayList<>();
		items= new ArrayList<>();
		objRefs = new ArrayList<>();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ArrayList<RoomItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<RoomItem> items) {
		this.items = items;
	}
	public void addToRoom(Room room) {
		room.getStimulus().addAll(getStimuluses());
		room.getExits().addAll(getExits());
		room.getRoomItems().addAll(getItems());
	}

	public String getType() {
		return type;
	}

	public ArrayList<ObjectRef> getObjRefs() {
		return objRefs;
	}
}
