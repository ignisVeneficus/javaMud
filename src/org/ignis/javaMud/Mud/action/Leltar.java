package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Item;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.Mud.utils.HolderUtils;
import org.ignis.javaMud.utils.Colorize;

public class Leltar extends Action {

	@Override
	public String getName() {
		return "leltár";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		Item[] list = source.getContains();
	
		List<String> sList = new ArrayList<>();
		int size = 0;
		for(Item i:list) {
			String shortName = i.getShortName().getWord();
			sList.add(shortName);
			size = Math.max(size, shortName.length());
		}
		if(size>39) {
			size = 39;
		}
		Collections.sort(sList, StringUtil.simpleCollator);
		
		sList = HolderUtils.orderResultList(sList);
		StringBuffer buff = new StringBuffer();
		if(sList.size()>0) {
			buff.append("Tárgyak nálad:");
			StringUtil.writeList(buff, sList, Defaults.Color_objects, Colorize.RESET);
		}
		else {
			buff.append("Nincs nálad semmi.");
		}
		source.tell(buff.toString());
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"leltár" +Colorize.RESET + "\nKilistázza a nálad lévő tárgyak listáját.\n";				
	}

}
