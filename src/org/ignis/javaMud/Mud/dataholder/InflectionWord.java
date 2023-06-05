package org.ignis.javaMud.Mud.dataholder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
/**
 * Ragozas
 * Jelenleg targyas ragozas van csak
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InflectionWord {
	/**
	 * Szo
	 */
	@XmlValue
	private String word;
	/**
	 * Targyas ragozasa
	 */
	@XmlAttribute
	private String targy;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getTargy() {
		return targy;
	}
	public void setTargy(String targy) {
		this.targy = targy;
	}
	public InflectionWord() {
		
	}
}

