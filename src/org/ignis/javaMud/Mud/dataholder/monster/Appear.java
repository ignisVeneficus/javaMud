package org.ignis.javaMud.Mud.dataholder.monster;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
/**
 * Szornyek megjeleneset definialo adat
 * amikor letrejon egy szorny, akkor egy event generalodik, ezt irja le
 */
public class Appear {
	/**
	 * Event-hez tartozo erzekszervi tesztek
	 */
	@XmlElement(name="sense")
	private List<SenseTest> senseTests;

	/**
	 * Leiras
	 */
	@XmlElement(name="descr")
	private String descr;
	
	@XmlTransient
	private Event event;
	public Event getEvent() {
		return event;
	}
	public void init(Living l) {
		event = Event.createSimpleSourceEvent(senseTests, l, descr, null, null);
	}

}
