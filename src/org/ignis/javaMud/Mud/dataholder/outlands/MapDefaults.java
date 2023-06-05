package org.ignis.javaMud.Mud.dataholder.outlands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.ignis.javaMud.Mud.data.Coordinate;

// atrakni xml-re
public class MapDefaults {
	static private LinkedHashSet<String> lineTypes = null;
	static private HashMap<String,HashSet<String>> lineRoomTypes = null;
	static private HashSet<String> cityLineType = null;
	public static LinkedHashSet<String> getLineTypes() {
		if(lineTypes==null) {
			lineTypes = new LinkedHashSet<>();
			lineTypes.add("folyo");
			lineTypes.add("ut");
		}
		return lineTypes;
	}
	public static HashMap<String, HashSet<String>> getLineRoomTypes() {
		if(lineRoomTypes == null) {
			lineRoomTypes = new HashMap<>();
			HashSet<String> set = new HashSet<>();
			set.add("tenger");
			lineRoomTypes.put("folyo", set);
			set = new HashSet<>();
			lineRoomTypes.put("ut", set);
		}
		return lineRoomTypes;
	}
	public static HashSet<String> getCityLineType() {
		if(cityLineType==null) {
			cityLineType = new HashSet<>();
			cityLineType.add("ut");
		}
		return cityLineType;
	}
	public static String getSlope(int meret){
		if(meret<4) return "enyhén ";
		if(meret<7) return "";
		if(meret<10) return "közepesen ";
		if(meret<13) return "meredeken ";
		return "nagyon meredeken ";
	}
	public static double getSPSlope(int meret){
		if(meret<0) {
			if(meret>-4) return 0.8;
			if(meret>-7) return 1.5;
			if(meret>10) return 2;
			if(meret>13) return 3;
			return 5;
			
		}
		else {
			if(meret<4) return 1.2;
			if(meret<7) return 1.5;
			if(meret<10) return 2;
			if(meret<13) return 3;
			return 5;
		}
	}
	public static String getDirection(int dir) {
		switch(dir) {
		case Coordinate.DIR_NONE: return "";
		case Coordinate.DIR_E: return "észak";
		case Coordinate.DIR_EK: return "északkelet";
		case Coordinate.DIR_K: return "kelet";
		case Coordinate.DIR_DK: return "délkelet";
		case Coordinate.DIR_D: return "dél";
		case Coordinate.DIR_DNY: return "délnyugat";
		case Coordinate.DIR_NY: return "nyugat";
		case Coordinate.DIR_ENY: return "északnyugat";
		}
		return "";
	}
	public static String getLineString(String type, int[] dir, String subtype, Integer[] height) {
		if("folyo".equals(type)) {
			int h=dir.length;
			if(h==1){
				return "Egy forrás található itt, és egy "+ getSubtype(type, subtype) + " "+ getDirection(dir[0]) + "felé folyik.";
			}
			else if(h==2){
				String[] direction = new String[2];
				if(height[0]>height[1]) {
					direction[0]= getDirection(dir[0]);
					direction[1]= getDirection(dir[1]);
				}
				else {
					direction[1]= getDirection(dir[0]);
					direction[0]= getDirection(dir[1]);
				}
				return "Egy "+ getSubtype(type, subtype) + " "+direction[0]+" felől "+direction[1]+" felé folyik.";
			}
			else{
				int max = 0;
				int irany=0;
				for(int i=0;i<h;i++) {
					// a magassag is egyenloseg alapjan nezi
					if(max<=height[i]) {
						max = height[i];
						irany=dir[i];
					}
				}
				String ret ="Egy " + getSubtype(type, subtype) + " " + getDirection(irany)+ " felől folyik és ";
				int qty=0;		
				for(int i=0; i<h; i++) {
					if(dir[i]!=irany) {
						if(qty == h-2) ret += "valamint ";
						ret += getDirection(dir[i]);
						if(qty != h-2) ret+=", ";
						qty++;
					}
				}
				ret +=" irányba ágazik el.";
				return ret;
			}
		}
		return "";
	}
	public static String getSubtype(String type, String subtype) {
		if("folyo".equals(type)) {
			if("er".equals(subtype)) return "ér";
			if("patak".equals(subtype)) return "patak";
			if("folyo".equals(subtype)) return "folyó";
			if("folyam".equals(subtype)) return "folyam";
		}
		if("ut".equals(type)) {
			if("osveny".equals(subtype)) return "ösvény";
			if("gyalogut".equals(subtype)) return "gyalogút";
			if("foldut".equals(subtype)) return "földút";
			if("kout".equals(subtype)) return "kövezett út";
		}
		return "";
	}
	public static String getSlopeString(int maxDir, int minDir, int up, int down, boolean hasMax, boolean hasMin) {
		String ret ="A terep ";
		if(hasMax) {
			ret += getDirection(maxDir) + " irányba " + getSlope(up) + "emelkedik";
			if(hasMin) {
				ret +=" és ";
			}
		}
		if(hasMin) {
			ret += getDirection(minDir) + " irányba " + getSlope(down) + "lejt";
		}
		ret +=".";
		return ret;
	}
	
}
