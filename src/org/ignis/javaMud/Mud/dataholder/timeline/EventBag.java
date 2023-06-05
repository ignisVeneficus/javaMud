package org.ignis.javaMud.Mud.dataholder.timeline;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.utils.xml.DiceIntegerAdapter;
/**
 * Egy adott idoben letrejovo tortenesek.
 * A tortenes letrejottekor a cel objektum tulajdonsaga es/vagy leirasa megvaltozik es errol mindenki a szobaban ertesitest kap
 * 
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EventBag implements Comparable<EventBag>{
	static private Logger LOG = LogManager.getLogger(EventBag.class);
	/**
	 * Esemenyek ideje
	 */
	@XmlAttribute(name = "time")
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer TS;
	/**
	 * tulajdonsag valtozasa
	 */
	@XmlElement(name="property")
	private List<PropertyEvent> properties;
	/**
	 * Erzekszervi leiras valtozas (pl a szaglas, hallas leiras valtozik)
	 */
	@XmlElement(name="stimulus")
	private List<Stimulus> stimulus;
	/**
	 * Esemeny, amivel az egesztrol tudomast szerez a jatekos
	 */
	@XmlElement(name="event")
	private List<Stimulus> events;


	private EventBag() {
		properties = new ArrayList<PropertyEvent>();
		events = new ArrayList<Stimulus>();
		stimulus = new ArrayList<Stimulus>();
		TS = 0;
	}

	@Override
	public int compareTo(EventBag o) {
		return (this.getTS() - o.getTS());
	}

	public int getTS() {
		return TS;
	}

	public List<PropertyEvent> getPropertiesList() {
		return properties;
	}

	public List<Stimulus> getEventsList() {
		return events;
	}

	public void init(Object obj) {
		for(PropertyEvent pe:properties) {
			pe.init(obj);
		}
		
	}

	public void handleEvent(Entity obj) {
		LOG.trace("Events...");
		for(PropertyEvent e:properties) {
			e.process(obj);
		}
		HashSet<String> senses = new HashSet<>();
		for(Stimulus s:stimulus) {
			senses.add(s.getType());
		}
		ArrayList<Stimulus> oldlist = obj.getStimulus();
		for(Stimulus s:oldlist) {
			if(senses.contains(s.getType())) {
				oldlist.remove(s);
			}
		}
		oldlist.addAll(stimulus);
	
		for(Stimulus t:events) {
			LOG.trace("Handle event");
			Event e = Event.createFromStimulus(t, obj);
			e.fire();
		}
	}
}
