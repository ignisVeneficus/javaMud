package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Nez extends Action {

	@Override
	public String getName() {
		return "néz";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		Holder env = source.getEnvironment();
		if(env==null) {
			return true;
		}
		// ha nincs parameter: korbenez -> szoba leirasa
		if(StringUtils.isBlank(paramLine)) {
			Event evn = Event.createSimpleSourceEvent(Defaults.Sense_Latas, env.getStimulus(Defaults.Sense_Latas), source, "%S körbenéz.", "", "Körbenéztél.");
			if(evn!=null) {
				evn.fire();
			}
			String 	descr = env.getDescription(source, true); 
			if(StringUtils.isNotBlank(descr)) {
				source.tell(descr);
			}
			return true;
		}
		
		List<String> params = parseCommand(paramLine, 0);
		// mit kell felvenni
		Entity target = null;
		List<Sense> senseList = source.getSenseByType(Defaults.senses);
		if(params.size()>1) {
			// megnezzuk a leltart
			String name = params.get(params.size()-1);
			if(StringUtil.equalsSecoundString("nalam", name)) {
				List<ItemPointer> poiList = parse(params.subList(0, params.size()-1));
				target = target(source, poiList, senseList,false);
				if(target!=null) {
					String descr = target.getDescription(source, true);
					if(StringUtils.isNotBlank(descr)) {
						source.tell(descr);
					}
					return true;
				}
			}
		}
		// szoba leltar
		List<ItemPointer> poiList = parse(params);
		target = target(env, poiList, senseList,true);
		if(target!=null) {
			String descr = target.getDescription(source, true);
			if(StringUtils.isNotBlank(descr)) {
				SenseTest t = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
				ArrayList<SenseTest> set = new ArrayList<>();
				set.add(t);
				Event e = Event.createSimpleSourceSubjectEvent(set, source, target,
						"%S megvizsgálja %T.",
						null,
						"%S matat valamit.",
						null,
						"Megvizsgálod %T.");
				if(e!=null) {
					e.fire();
				}
				source.tell(descr);
			}
			return true;
		}
		// szoba sajat kiertekelese
		// idojaras, roomitemek, stb..
		if( env.look(params.get(0), source)) {
			return true;
		}
		
		// sajat kiertekeles, pl test megnezese
		if( source.look(params.get(0), source)) {
			return true;
		}
		
		source.tell("Olyat nem találsz.");
		return true;
		
	}
	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"néz" +Colorize.RESET + " " + Defaults.Color_command_arg + "[<mit>]" + Colorize.RESET + " " +Defaults.Color_command_arg + "[<hol>]" + Colorize.RESET + "\n" +
				"Megvizsgálhatsz tárgyakat, lényeket.\nHa nem adod meg a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " és a " +Defaults.Color_command_arg + "<hol>" + Colorize.RESET+"-t, akkor az adott területen nézel körbe.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd megvizsgálni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"néz tőr 2" + Colorize.RESET +"\n"+
				"Ha a "+Defaults.Color_command_arg + "<hol>" + Colorize.RESET+" " +Defaults.Color_command_arg + "nalam" + Colorize.RESET+ " akkor a nálad lévő tárgyat vizsgálod meg.";
	}

}
