package org.ignis.javaMud.Mud.dataholder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.utils.xml.TestAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(TestAdapter.class)
abstract public class Test {
	public final static String T_SENSE = "SE";
	public final static String T_STAT  = "ST";
	public final static String T_SKILL = "SK";
	/**
	 * tipusa:
	 * SE: sense: erzekek
	 * ST: stat: tulajdonsagok
	 * SK: skill: kepzettsegek
	 */
	@XmlAttribute
	private String type;
	/**
	 * Nev amit tesztelni kell
	 */
	@XmlAttribute
	private String name;
	/**
	 * celszam amire tesztelni kell
	 */
	@XmlAttribute
	private int difficulty;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public Test() {
		type="SE";
	}
	public Test(String type, String name, int difficulty) {
		super();
		this.type = type;
		this.name = name;
		this.difficulty = difficulty;
	}
	public String toString() {
		return "Type: " + type + " Name: " + name + " Difficulty: " + difficulty;
	}
	abstract public boolean doTest(Living obj);
	public void changeDifficulty(int amouth) {
		difficulty+=amouth;
	}
	
}
