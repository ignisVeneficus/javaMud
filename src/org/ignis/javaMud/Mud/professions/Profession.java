package org.ignis.javaMud.Mud.professions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Monster;
import org.ignis.javaMud.Mud.action.Mond;

public abstract class Profession {
	static public final List<String> parse(String what) {
		ArrayList<String> ret = new ArrayList<String>();
        Matcher m = Mond.mondPattern.matcher(what);
        if (m.matches()) {
        	String who = m.group(1);
        	String text = m.group(2);
        	ret.add(who);
        	Collections.addAll(ret, text.split(" "));
        }
        return ret;
	}
	public abstract boolean action(String command, String[] param, Living source) ;
	
	public abstract boolean handle(List<String> line, Monster obj);
	
	public abstract void init(Map<String,String[]> data);
}
