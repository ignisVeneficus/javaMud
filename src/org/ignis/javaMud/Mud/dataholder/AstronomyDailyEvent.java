package org.ignis.javaMud.Mud.dataholder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class AstronomyDailyEvent implements Comparable<AstronomyDailyEvent>{
	/**
	 * kezdete ido (perc)
	 */
	@XmlAttribute
	private int time;
	/**
	 * event megnevezese
	 */
	@XmlAttribute
	private String name;
	/**
	 * leiras, amit megkapnak a szobakban, p; :felkelt a nap
	 */
	private String description;
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public AstronomyDailyEvent() {
		
	}
	public AstronomyDailyEvent(int time, String name, String description) {
		this.time = time;
		this.name = name;
		this.description = description;
	}
	@Override
	public int compareTo(AstronomyDailyEvent o) {
		return this.getTime() - o.getTime();
	}
	
}
