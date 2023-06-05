package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Alias extends Action {

	@Override
	public String getName() {
		return "alias";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if((source==null)|| !(source instanceof Player)) {
			return true;
		}
		List<String> params = parseCommand(paramLine, 2);
		Player p = (Player)source;
		HashMap<String,String> map = p.getAlias();
		if(params.size() ==0) {
			
			ArrayList<String> keys = new ArrayList<String>(map.keySet());
			
			if(keys.size()==0) {
				p.tell("Nincsenek rövidítések beállítva!");
				return true;
			}
			StringBuffer buff = new StringBuffer();
			buff.append("Beállított rövidítések:");
			Collections.sort(keys, StringUtil.simpleCollator);
			for(String s:keys) {
				buff.append("\n");
				buff.append(Defaults.Color_command);
				buff.append(s);
				buff.append(Colorize.RESET);
				buff.append(" -> ");
				buff.append(Defaults.Color_command_arg);
				buff.append(map.get(s));
				buff.append(Colorize.RESET);
			}
			p.tell(buff.toString());
			return true;
		}
		if(params.size() ==1) {
			String key = params.get(0);
			
			if(map.containsKey(key)) {
				map.remove(key);
				p.tell("A "+Defaults.Color_command_arg+ key + Colorize.RESET+ " rövidítés törölve." );
				return true;
			}
			p.tell("Nem található ilyen rövidítés!");
		}
		if(params.size() ==2) {
			String key = params.get(0);
			String val = params.get(1);
			boolean has = map.containsKey(key);
			if(map.containsKey(val)) {
				p.tell("A "+ Defaults.Color_command_arg+ val + Colorize.RESET+ " már létező rövidítés!");
				return true;
			}
			map.put(key, val);
			p.tell("A "+ Defaults.Color_command_arg+ key + Colorize.RESET+ " rövidítés " + (has?"lecserélve":"felvéve")+".");
			return true;
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"alias"+Colorize.RESET + " " + Defaults.Color_command_arg + "[<rövidítés>] [<parancs>]" +Colorize.RESET + "\nParancsrövidítéseket lehet vele kezelni.\n"+
				Defaults.Color_command+"alias"+Colorize.RESET + ": kilistázza a meglévő rövidítéseidet.\n"+
				Defaults.Color_command+"alias <rövidítés>"+Colorize.RESET + ": törli " +Defaults.Color_command_arg +"<rövidítés>"+ Colorize.RESET+" rövidítést.\n"+
				Defaults.Color_command+"alias <rövidítés> <parancs>"+Colorize.RESET + ": beállítja a  " +Defaults.Color_command_arg +"<parancs>"+ Colorize.RESET+" parancshoz a "+Defaults.Color_command_arg +"<rövidítés>"+ Colorize.RESET+" rövidítést.\n"+
				"A "+Defaults.Color_command_arg +"<parancs>"+ Colorize.RESET+" lehet már meglévő rövidítés.";
	}

}
