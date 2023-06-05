package org.ignis.javaMud.Mud.dataholder.outlands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringColor implements Comparable<StringColor>{
	static private Logger LOG = LogManager.getLogger(StringColor.class);
	private static final Pattern p = Pattern.compile("^(([0-9a-fA-F]{2})|(__))(([0-9a-fA-F]{2})|(__))(([0-9a-fA-F]{2})|(__))$");
	private String str;
	public StringColor(int color) {
		str = String.format("%06X", (0xFFFFFF & color));
	}
	private StringColor() {
		str = "______";
	}
	private void setStr(String str) {
		this.str = str;
	}
	public String toString() {
		return str;
	}
	@Override
	public int compareTo(StringColor o) {
		return str.compareTo(o.toString());
	}
	@Override
	public int hashCode() {
		return str.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StringColor) {
			return toString().equals(obj.toString());
		}
		return str.equals(obj);
	}
	static public StringColor createFromString(String str) {
		Matcher m = p.matcher(str);
		StringColor ret = new StringColor();
		if(m.find()) {
			ret.setStr(StringUtils.upperCase(str));
		}
		else {
			LOG.error("NOT valid StringColor: " + str);
		}
		return ret;
	}
	public ArrayList<StringColor> explode(){
		ArrayList<StringColor> ret = new ArrayList<>();
		String red = str.substring(0,2);
		if(!"__".equals(red)) {
			StringColor sc = new StringColor();
			sc.setStr(red+"__" + "__");
			ret.add(sc);
		}
		String green = str.substring(2,4);
		if(!"__".equals(green)) {
			StringColor sc = new StringColor();
			sc.setStr("__"+ green+"__");
			ret.add(sc);
		}
		String blue = str.substring(4,6);
		if(!"__".equals(blue)) {
			StringColor sc = new StringColor();
			sc.setStr("__"+"__" + blue);
			ret.add(sc);
		}
		return ret;
	}
}
