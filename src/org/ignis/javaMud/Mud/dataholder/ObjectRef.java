package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
/**
 * 
 * @author Csaba Toth
 * Objektum referencia az XML tarolashoz.
 * A szobakhoz tartozo lenyeket/targyakat tartalmazza
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ObjectRef {
	@XmlAttribute
	private String name;
	/**
	 * Darabszam, amennyinek meg kell jelennie
	 */
	@XmlAttribute
	private int qty;
	
	/**
	 * Hasznalat, vagyis az adott targy hasznalatban van-e
	 */
	@XmlAttribute
	private boolean used;
	
	/**
	 * Az objektum tartalma, ha lehet neki. Ladak tartalma/lenyek cuccai
	 */
	@XmlElement(name = "objRef")
	private List<ObjectRef> content;
	/* *********************************************
	 * localis valtozok
	 * 
	 * *********************************************/
	
	public ObjectRef() {
		content = new ArrayList<>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public List<ObjectRef> getContent() {
		return content;
	}
	public void setContent(ArrayList<ObjectRef> content) {
		this.content = content;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	
}
