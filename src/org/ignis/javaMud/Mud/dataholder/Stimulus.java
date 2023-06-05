package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.utils.StringFunctions;
/**
 * 
 * @author Csaba Toth (csaba.toth@sptech.ch)
 * Leirasok tarolas
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Stimulus {
	/**
	 * tipusa: latas/hallas/szaglas stb
	 */
	@XmlAttribute
	//@XmlJavaTypeAdapter(CommaSeparatedListAdapter.class)
	private String type;
	/**
	 * Intenzitas - szaglas/hallas/magia eseten a forras erossege is, latas eseten mennyire lehet eszrevenni
	 */
	@XmlAttribute
	private Integer intensity;
	/**
	 * Forras erossege latasnal, mennyi fenyt ad ki a targy
	 */
	@XmlAttribute
	private int source;
	/**
	 * Leirasa
	 */
	private String descr;
	/**
	 * Rovid leiras
	 */
	@XmlAttribute
	private String shortDescr;
	/**
	 * Feltetel, adott listaban meglete eseten jelenik meg. ilyen listat allit elo pl az astronomy, este, nappal, ejjel, ejfel stb
	 */
	@XmlAttribute
	private String condition;
	
	@XmlTransient
	private boolean hasCallback;
	
	
	
	public Stimulus(String type, int intensity, String descr,String shortDescr, String condition, int source) {
		super();
		this.type = type;
		this.intensity = intensity;
		this.descr = descr;
		this.shortDescr = shortDescr;
		this.condition = condition;
		this.source = source;
	}
	public Stimulus(String type, int intensity, String descr,String shortDescr, String condition, int source, boolean hasCallback) {
		super();
		this.type = type;
		this.intensity = intensity;
		this.descr = descr;
		this.shortDescr = shortDescr;
		this.condition = condition;
		this.source = source;
		this.hasCallback = hasCallback;
		
	}
	public Stimulus() {
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type=type;
	}
	public int getIntensity() {
		if(intensity==null) return 100;
		return intensity;
	}
	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}
	public String getDescr(Object obj) {
		if(hasCallback) {
			return StringFunctions.processCallback(obj, descr);
		}
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getShortDescr(Object obj) {
		if(StringUtils.isBlank(shortDescr)) return getDescr(obj);
		return shortDescr;
	}
	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Stimulus clone() {
		return new Stimulus(type, intensity, descr, shortDescr, condition,source,hasCallback);
	}
	@Override
	public String toString() {
		return "{type: " + StringUtils.join(type,";") + " intensity:" + intensity + " descr: " + descr + " shortDescr: " + shortDescr + " condition:" + condition + " source: " +source + "}";
	}
	
	//descr = StringFunctions.process(this,descr);

	void afterUnmarshal(Unmarshaller u, java.lang.Object parent) {
		Object o = null;
		init(o);
	}
	
	public void init(Object obj) {
		if(StringUtils.isNotBlank(descr)) {
			descr = StringFunctions.process(obj,descr);
			hasCallback = StringFunctions.checkCallback(descr);
		}
	}
	/**
	 * nem sajat forrassal rendelkezo kornyezeti ertek, az egesz osszessege
	 * @param in bemeneti lista
	 * @param env "kornyezet" erteke
	 * @return az osszes ertek osszege
	 */
	public static int getRefStimulus(ArrayList<Stimulus> in,int env) {
		int ret = env;
		for(Stimulus s:in) {
			int intensity = s.getSource();
			ret += intensity;
		}
		return ret;
	}
	/**
	 * Sajat forrassal rendelkezo kornyezeti ertekek
	 * @param in bemeneti lista
	 * @return az osszes maximuma
	 */
	public static int getOwnStimulus(ArrayList<Stimulus> in) {
		int ret = 0;
		for(Stimulus s:in) {
			ret = Math.max(ret,s.getIntensity());
		}
		return ret;
	}
	public int getSource() {
		return source;
	}
}
