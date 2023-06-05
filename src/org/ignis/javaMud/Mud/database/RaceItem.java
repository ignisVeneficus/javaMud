package org.ignis.javaMud.Mud.database;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.ignis.javaMud.Mud.Core.Bonus;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.db.Race;

public class RaceItem extends DatabaseItem<Race> {
	@XmlElement(name="sense")
	private ArrayList<Sense> senses;
	@XmlAttribute(name="bodyType")
	private String bodyType;
	@XmlAttribute(name="size")
	private String size;
	private ArrayList<Bonus> bonuses;
	
	@Override
	public Race createOne() {
		// TODO Auto-generated method stub
		return null;
	}

}
