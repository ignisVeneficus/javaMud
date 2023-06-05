package org.ignis.javaMud.Mud.dataholder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
/**
 * Felteteles leiras, adott szoba feltetelhez kotodik, pl "ejszaka" vagy "szilveszter" stb..
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionDescr {
	/**
	 * Leiras
	 */
	@XmlValue
	private String descr;
	/**
	 * Feltetel
	 */
	@XmlAttribute
	private String condition;
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public ConditionDescr() {
		
	}
}
