package org.ignis.javaMud.Mud.Core;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ignis.javaMud.utils.xml.DiceIntegerAdapter;

/**
 * Skill, stat bonuszok
 * 
 * @author Csaba Toth (csaba.toth@sptech.ch)
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Bonus {
	/**
	 * Bonusz neve, amilyen ertekre vonatkozik
	 */
	@XmlAttribute
	private String name;
	/**
	 * Erteke, ami lehet veletlenszeru is
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer value;
	
	/**
	 * Tipusa: stat v skill v Sense
	 */
	@XmlAttribute
	private String type;
	/**
	 * megnevezese, hogy tul keppen ez micsoda
	 */
	@XmlAttribute
	private String description;

	/**
	 * Azonositoja a bonusznak.
	 * egy adott id-ju bonusz csak egyszer adhato
	 */
	@XmlAttribute
	private String id;
	
	/**
	 * milyen objektum adta
	 */
	@XmlTransient
	private java.lang.Object source;
	
	
	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public String getType() {
		return type;
	}
	void afterUnmarshal(Unmarshaller unmarshaller, java.lang.Object parent) {
		if(value==null) {
			value=0;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public java.lang.Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}
}
