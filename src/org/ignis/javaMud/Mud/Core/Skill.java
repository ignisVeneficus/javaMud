package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.utils.Defaults;

// kellene egy parent, amit meghiv minden valtozasnal, hogy a szmitott ertekeket at lehessen vezetni.
// tamadas + vedekezes: szum(i:0-skill){ (10^(i/(30-(i/10) ) ) ) }

@XmlAccessorType(XmlAccessType.FIELD)
public class Skill extends BonusHandler implements Comparable<Skill> {
	public static final int RES_UNSUCCESS_KRIT = 0;
	public static final int RES_UNSUCCESS_FAIL = 1;
	public static final int RES_UNSUCCESS_MISH = 2;

	public static final int RES_SUCCESS_SMALL = 10;
	public static final int RES_SUCCESS_NORM = 11;
	public static final int RES_SUCCESS_BIG = 12;
	
	public static final int RES_SUCCESS_PROF_NORM = 20;
	public static final int RES_SUCCESS_PROF_BIG = 21;
	public static final int RES_SUCCESS_PROF_PERF = 22;
	/**
	 * pont amibol fejlesztes lesz
	 */
	@XmlAttribute
	private int points;
	/**
	 * tehetseg lista
	 */
	@XmlElement(name="talent")
	private ArrayList<String> talents;
	/**
	 * hanyszor probalta:
	 * sikertelen +1, siker +5
	 */
	@XmlAttribute
	private int trys;
	/**
	 * Kepzettseg neve
	 */
	@XmlAttribute
	private String name;
	/**
	 * nehezsege
	 */
	@XmlAttribute
	private int difficulty;
	
	public Skill(String name, int difficulty, ArrayList<String> talents) {
		super(name,1);
		this.name = name;
		this.difficulty = difficulty;
		this.talents = talents;
		trys = 0;
		points = 0;
	}
	
	public int test(int agains) {
		int ret = 0;
		synchronized (this) {
			int nr1 = ThreadLocalRandom.current().nextInt(0, 10);
			int nr2 = ThreadLocalRandom.current().nextInt(0, 10);
			int rn1= nr1*10 + nr2;
			int rn2= nr2*10 + nr1;
			int level = Math.max(Math.min(getValue(), 100),0);
			int result = test(agains,rn1,level) * test(agains,rn2,level);
			switch(result) {
			case 1: 
				ret = RES_UNSUCCESS_KRIT;
				trys +=1;
				break;
			case 2:
				ret= RES_UNSUCCESS_FAIL;
				trys +=1;
				break;
			case 3:
				ret= RES_UNSUCCESS_MISH;
				trys +=1;
				break;
			
			case 4:
				ret= RES_SUCCESS_SMALL;
				trys +=5;
				break;
			case 6: 
				ret = RES_SUCCESS_NORM;
				trys +=5;
				break;
			case 9: 
				ret = RES_SUCCESS_BIG;
				trys +=5;
				break;

			case 10:
				ret= RES_SUCCESS_PROF_NORM;
				trys +=10;
				break;
			case 15: 
				ret = RES_SUCCESS_PROF_BIG;
				trys +=10;
				break;
			case 25: 
				ret = RES_SUCCESS_PROF_PERF;
				trys +=10;
				break;
			}
			int incr = Defaults.getSkillTryPerPoint();
			if(trys >= incr) {
				int add = Math.floorDiv(trys, incr);
				points += add;
				trys = trys%incr;
			}
		}
		return ret;
	}
	static private int test(int agains, int nr, int level) {
		// pontos
		if((nr>agains)&&(nr<=level)) return 5;
		// kudarc
		if((nr>level)&&(nr<=agains)) return 1;
		// szerencse
		if(nr>agains) return 2;
		// tudas
		if(nr<=level) return 3;
		return 1;
	}
	
	public void setBaseValue(int level) {
		if(level<0)level=0;
		if(level>100) level=100;
		super.setBaseValue(level);
	}

	public int getPoints() {
		return points;
	}

	public ArrayList<String> getTalents() {
		return talents;
	}

	public String getName() {
		return name;
	}

	public int getDifficulty() {
		return difficulty;
	}
	public static boolean isSuccess(int res) {
		return res>=RES_SUCCESS_SMALL;
	}
	public static boolean isProf(int res) {
		return res>=RES_SUCCESS_PROF_NORM;
	}

	@Override
	public int compareTo(Skill o) {
		return StringUtil.simpleCollator.compare(getName(),o.getName());
	}

	@Override
	public String getType() {
		return Defaults.Name_Skill;
	}

}
