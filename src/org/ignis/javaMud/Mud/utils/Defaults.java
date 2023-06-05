package org.ignis.javaMud.Mud.utils;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.dataholder.Test;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.utils.Colorize;
// atirni xml-esre
public class Defaults {
	public static final String Sense_Latas = "latas";
	public static final String Sense_Hallas = "hallas";
	public static final String Sense_Szaglas = "szaglas";
	public static final String Sense_Magia = "magia";

	
	public static final String Color_latas = Colorize.C_B_GREEN;
	public static final String Color_hallas = Colorize.C_CYAN;
	public static final String Color_szaglas = Colorize.C_MAGENTA;
	public static final String Color_magia = Colorize.C_B_CYAN;
	public static final String Color_exits = Colorize.C_B_BLUE;
	public static final String Color_livings = Colorize.C_ORANGE;
	public static final String Color_objects = Colorize.C_YELLOW;
	
	public static final String Color_command = Colorize.C_YELLOW;
	public static final String Color_command_arg = Colorize.C_ORANGE;

	public static final String Sebzes_uto = "uto";
	public static final String Sebzes_vago = "vago";
	public static final String Sebzes_szuro = "szuro";
	public static final String Sebzes_tuz = "tuz";
	public static final String Sebzes_fagy = "fagy";
	public static final String Sebzes_lelek = "lelek";
	
	

	public static final HashSet<String> senses = new HashSet<>();
	static {
		senses.add(Defaults.Sense_Latas);
		senses.add(Defaults.Sense_Hallas);
		senses.add(Defaults.Sense_Szaglas);
		senses.add(Defaults.Sense_Magia);
	}
	
	public static final String Name_Sense = "erzek";
	public static final String Name_Skill = "kepzettseg";
	public static final String Name_Stat = "kepesseg";

	
	public static final String Bonus_Sense = "sense";
	public static final String Bonus_Skill = "skill";
	public static final String Bonus_Stat = "stat";
	
	
	public static final int RestMultiple = 10;
	
	
	static final public int getDefaultIntensity(String type) {
		if(StringUtils.isBlank(type)) return 0;
		String t = StringUtil.exEkezet(type);
		if(t.equals(Sense_Latas)) {
			return 100;
		}
		if(t.equals(Sense_Hallas)) {
			return 50;
		}
		if(t.equals(Sense_Szaglas)) {
			return 50;
		}
		if(t.equals(Sense_Magia)) {
			return 50;
		}
		return 0;
	}
	
	static final public String getDefaultDescriptionForSense(String type, int result) {
		if(StringUtils.isBlank(type)) return "";
		String t = StringUtil.exEkezet(type);
		if(t.equals(Sense_Latas)) {
			switch(result) {
			case(Perception.NONE_BELOW):
				return "Túl sötét van, hogy bármit is lássál.";
			case(Perception.SOME_BELOW):
				return "Alig látsz a félhomályban.";
			case(Perception.SOME_ABOVE):
				return "Alig látsz az erős fényben.";
			case(Perception.NONE_ABOVE):
				return "Túl világos van, hogy bármit is lássál.";
			}
		}
		if(t.equals(Sense_Hallas)) {
			switch(result){
			case(Perception.NONE_BELOW):
				return "Csend van.";
			case(Perception.SOME_BELOW):
				return "Valami halk hangot hallasz.";
			}
		}
		if(t.equals(Sense_Szaglas)) {
			switch(result){
			case(Perception.NONE_BELOW):
				return "Nem érzel semmi illatot";
			case(Perception.SOME_BELOW):
				return "Valami halvány illatot érzel.";
			}
		}
		return null;
	}
	static final public String numberToText(int number) {
		switch(number) {
		case 2:
			return "kettő ";
		case 3:
			return "három ";
		case 4:
			return "négy ";
		case 5:
			return "öt ";
		case 6:
			return "hat ";
		case 7:
			return "hét ";
		case 8:
			return "nyolc ";
		case 9:
			return "kilenc ";
		case 10:
			return "tíz ";
		}
		if(number>=10) return "sok "; 
		
		return "";
	}
	/**
	 * Default SP vesztes mezei kijaratoknal
	 * @return
	 */
	public static final int getDefaultSPLost() {
		return 5;
	}
	/**
	 * Default SP vesztes uszas eseten, ha sikerult
	 * @return
	 */
	public static final int getSwimSPLost() {
		return 10;
	}
	/**
	 * Default HP vesztes fulladas eseten
	 * @return
	 */
	public static final int getDrownHPLost() {
		return 15;
	}
	/**
	 * Alap uszasi celszam
	 * @return
	 */
	public static final int getSkillSwimTarget() {
		return 15;
	}

	/**
	 * Alap erzek teszteket allit elo az erzekek neveit tartalmazo listabol
	 * Az alap eszelelhetoseget hasznalja
	 * @param sense az erzekek listaja, [,; ] -el lehet elvalasztva
	 * @return
	 */
	static public ArrayList<SenseTest> getDefaultTestFor(String sense){
		ArrayList<SenseTest> ret = new ArrayList<>();
		String[] strl = sense.split("[,; ]");
		for(int i =0;i<strl.length;i++) {
			SenseTest s = new SenseTest(strl[i], getDefaultIntensity(strl[i]));
			ret.add(s);
		}
		return ret;
	}
	public static final int getSkillTryPerPoint() {
		return 100;
	}
	public static final String getHealthDescription(double value) {
		if(value<=0.15) {
			return Colorize.C_RED+"Már-már halott."+Colorize.RESET;
		}
		if(value<=0.29) {
			return Colorize.C_B_RED+"Borzasztóan sérült."+Colorize.RESET;
		}
		if(value<=0.44) {
			return Colorize.C_ORANGE+"Csúnyán sebződött."+Colorize.RESET;
		}
		if(value<=0.59) {
			return Colorize.C_YELLOW+"Jócskán sérült."+Colorize.RESET;
		}
		if(value<=0.74) {
			return Colorize.C_B_CYAN+"Kissé sérült."+Colorize.RESET;
		}
		if(value<=0.74) {
			return Colorize.C_GREEN+"Tűrhető állapotban"+Colorize.RESET+" van.";
		}
		return Colorize.C_B_GREEN+"Csúcsformában"+Colorize.RESET+" van.";
	}
}
