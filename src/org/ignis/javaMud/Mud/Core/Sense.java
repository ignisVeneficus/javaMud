package org.ignis.javaMud.Mud.Core;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.utils.Defaults;

/**
 * 
 * @author Csaba Toth
 * Erzekek tarolasa
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Sense extends BonusHandler implements Comparable<Sense>{
	/**
	 * tipus, sajat forras, vagy visszaverodes
	 * (kornyezethez szukseges) 
	 */
	@XmlAttribute(name="type")
	private int perceptionType;
	/**
	 * kozepertek
	 */
	@XmlAttribute
	private int mean;
	
	/**
	 * tipusa: latas, hallas, stb
	 */
	@XmlAttribute
	private String senseType;
	
	public Sense() {
		super();
	}
	
	private Sense(String name, int perceptionType, int mean, int value, String senseType, List<Bonus> bonus) {
		super(name,value);
		this.perceptionType = perceptionType;
		this.mean = mean;
		this.senseType = senseType;
		addBonus(bonus);
		
	}
	public Sense(String name, int perceptionType, int mean, int value, String senseType) {
		super(name,value);
		this.perceptionType = perceptionType;
		this.mean = mean;
		this.senseType = senseType;
		
	}

	public int getPerceptionType() {
		return perceptionType;
	}
	public void setPerceptionType(int type) {
		this.perceptionType = type;
	}
	public int getMean() {
		return mean;
	}
	public void setMean(int mean) {
		this.mean = mean;
	}
	public String getSenseType() {
		return senseType;
	}
	public void setSenseType(String senseType) {
		this.senseType = senseType;
	}
	@Override
	public String toString() {
		return "{name: " +getName() + " senseType: " + senseType + " mean: " + mean + " (with bonus: " + getMean() + ") variance: " + getValue() + "}";
	}

	@Override
	public String getType() {
		return Defaults.Name_Sense;
	}

	@Override
	public int compareTo(Sense o) {
		return StringUtil.simpleCollator.compare(getName(),o.getName());
	}
	
	public Sense clone() {
		return new Sense(getName(),getPerceptionType(),getMean(),getBaseValue(),getSenseType(),getBonuses());
	}
	
}
