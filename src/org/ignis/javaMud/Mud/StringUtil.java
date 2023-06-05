package org.ignis.javaMud.Mud;

import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringUtil {
	static private Logger LOG = LogManager.getLogger( StringUtil.class);
	private static final String[] from = {"á","à","â","ä","ǎ","ă","ā","ã","å","ǻ","ą","æ","ǽ","ɓ","ć","ċ","ĉ","č","ç","ď","đ","ɗ","ð",
			"é","è","ė","ê","ë","ě","ĕ","ē","ę","ẹ","ə","ɛ","ġ","ĝ","ğ","ģ","ĥ","ħ","ı","í","ì","î","ï","ǐ","ĭ","ī","ĩ","į","ị",
			"ĵ","ķ","ĸ","ĺ","ļ","ł","ľ","ŀ","ŉ","ń","ň","ñ","ņ","ŋ","ó","ò","ô","ö","ǒ","ŏ","ō","õ","ő","ọ","ø","ǿ","ơ","œ","ŕ","ř","ŗ","ſ",
			"ś","ŝ","š","ş","ș","ß","ť","ţ","ŧ","þ","ú","ù","û","ü","ǔ","ŭ","ū","ũ","ű","ů","ų","ụ","ư","ẃ","ẁ","ŵ","ẅ","ý","ỳ","ŷ","ÿ","ỹ","ź","ż","ž"};
	private static final String[] to = {"a","a","a","a","a","a","a","a","a","a","a","a","a","b","c","c","c","c","c","d","d","d","d",
			"e","e","e","e","e","e","e","e","e","e","e","e","g","g","g","g","h","h","i","i","i","i","i","i","i","i","i","i","i",
			"j","k","k","l","l","l","l","l","n","n","n","n","n","n","o","o","o","o","o","o","o","o","o","o","o","o","o","o","r","r","r","r",
			"s","s","s","s","s","s","t","t","t","t","u","u","u","u","u","u","u","u","u","u","u","u","u","w","w","w","w","y","y","y","y","y","z","z","z"};
	
	
	public static String exEkezet(String input) {
		if(input==null) return null;
		return StringUtils.replaceEach(input.toLowerCase(),from,to);
	}
	
	public static boolean equalsString(String one,String two) {
		return exEkezet(one).equals(exEkezet(two));
	}
	public static boolean equalsSecoundString(String one,String two) {
		return one.equals(exEkezet(two));
	}
	public static boolean equalsSecoundString(String one,List<String> two) {
		for(String str:two) {
			if(one.equals(exEkezet(str))) return true;
		}
		return false;
	}
	
	private static final String rule = "< a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS" + 
			" < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J" + 
			" < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó " + 
			" < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T" + 
			" < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS";
	
	public static RuleBasedCollator simpleCollator = null;
	static{
		try {
			simpleCollator=  new RuleBasedCollator(rule);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public static String listToString(List<String> list) {
		int last = list.size() - 1;
		if(last==-1) return "";
		if(last==0) return list.get(0);

		return String.join(", ", list.subList(0, last)) + " és " + list.get(last);

		
	}
	public static String filterInput(String input) {
		return input.replaceAll("[^\\p{InBasic_Latin}áéíóöőúüűÁÉÍÓÖŐÚÜŰ]", "");
	}

	public static String drawLine(double percent,int size) {
		if(percent<0) percent = 0;
		String bar = " "+ "▏"+ "▎"+ "▍"+ "▌"+ "▋"+ "▊"+ "▉"; // " ", "▏", "▎", "▍", "▌", "▋", "▊", "▉"
		int whole = (int)Math.floor(percent*size);
		int part = (int)Math.floor(((percent*size) % 1) * 8);
		int empty = size-whole-1;
		LOG.trace("percent: " + percent + " whole " + whole + " part: " + part + " empty: " + empty); 
		return ((whole>0)?StringUtils.repeat("█", whole):"") + ((whole<size)?bar.substring(part, part+1):"") + ((empty>0)?StringUtils.repeat(" ", empty):"");
	}
	// bar
	// " ",     "▐","█","▌"," "
	public static String drawLine(double from, double to,int size) {
		if(from<0) from = 0;
		if(to>1) to = 1;
		String bar1 = " "+  "▐"; 
		String bar2 = " "+  "▌"; // " ", "▏", "▎", "▍", "▌", "▋", "▊", "▉"
		int fempty = (int)Math.floor(from*size);
		int fpart = (int)Math.floor(((from*size) % 1) * 2);
		int whole = (int)Math.floor(to*size)-fempty-1;
		int tpart = (int)Math.floor(((to*size) % 1) * 2);
		int tempty = size-fempty-whole-2;
		LOG.trace("from: " + from + " to: "+to + " " +fempty + " | " + fpart + " | " + whole + " | " + tpart +" | " + tempty);
		if(fpart+tpart+whole<0) {
			tpart = 1;
		}
		return ((fempty>0)?StringUtils.repeat(" ", fempty):"")+
				((fempty<size)?bar1.substring(fpart, fpart+1):"")+
				((whole>0)?StringUtils.repeat("█", whole):"")+		
				(((fempty+whole+1)<size)?bar2.substring(tpart, tpart+1):"")+
				((tempty>0)?StringUtils.repeat(" ", tempty):"");
	}
	
	public static void writeList(StringBuffer buff,List<String> list) {
		writeList(buff, list, "", "");
	}
	public static void writeList(StringBuffer buff,List<String> list,String prefix, String sufix) {
		Collections.sort(list, StringUtil.simpleCollator);
		int length = 0;
		for(String st:list) {
			length = Math.max(length, st.length());
		}
		int row = Math.floorDiv(80, (length+1));
		int pos = 0;
		for(String st:list) {
			if(pos % row == 0) {
				buff.append("\n");
			}
			buff.append(prefix);
			buff.append(String.format("%1$-"+(length)+"s", st));
			buff.append(sufix);
			buff.append(" ");
			pos++;
		}
	}

}
