package org.ignis.javaMud.Mud.database;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.ignis.javaMud.Mud.Core.Skill;

public class SkillItem extends DatabaseItem<Skill> {
	@XmlElement(name="talent")
	private ArrayList<String> talents;
	/**
	 * nehezsege
	 */
	@XmlAttribute
	private int difficulty;
	
	@Override
	public Skill createOne() {
		Skill ret = new Skill(getName(),difficulty,talents);
		return ret;
	}

}
