package org.ignis.javaMud.Mud.handlers;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.action.*;
import org.ignis.javaMud.Mud.action.admin.Goto;


public class ActionHandler implements Handler {
	public static final String REG_NAME = "Actions";
	private HashMap<String,Action> commonMap;
	private HashMap<String,HashMap<String,Action>> classMap;
	private HashMap<String,HashMap<String,Action>> raceMap;
	
	@Override
	public void init(Engine e) {
		commonMap =  new HashMap<>();
		classMap = new HashMap<>();
		raceMap = new HashMap<>();
		// TODO Auto-generated method stub
		
		addAction(commonMap, new Alias());
		addAction(commonMap, new Eldob());
		addAction(commonMap, new Eltesz());
		addAction(commonMap, new Erzekek());
		addAction(commonMap, new Eszik());
		addAction(commonMap, new Felkel());
		addAction(commonMap, new Felvesz());
		addAction(commonMap, new Kepzettsegeim());
		addAction(commonMap, new Kivesz());
		addAction(commonMap, new Leltar());
		addAction(commonMap, new Mond());
		addAction(commonMap, new Nez());
		addAction(commonMap, new Pihen());
		addAction(commonMap, new Pont());
		addAction(commonMap, new Sugo());
		
		//admin
		addAction(commonMap,new Goto());
		
	}

	@Override
	public void dest() {
		// TODO Auto-generated method stub
		
	}
	
	public Action getRaceAction(String race, String first) {
		HashMap<String,Action> map = raceMap.get(race);
		if(map!=null) {
			return map.get(first);
		}
		return null;
	}
	public Action getClassAction(String rpgClass, String first) {
		if(StringUtils.isEmpty(rpgClass)) return null;
		HashMap<String,Action> map = classMap.get(rpgClass);
		if(map!=null) {
			return map.get(first);
		}
		return null;
	}
	public Action getAction(String first) {
		return commonMap.get(first);
	}
	
	private void addAction(HashMap<String,Action> map, Action a) {
		map.put(StringUtil.exEkezet(a.getName()), a);
	}
	public Action[] getAllAction() {
		Action[] ret = new Action[commonMap.size()];
		ret = commonMap.values().toArray(ret);
		return ret;
	}
}
