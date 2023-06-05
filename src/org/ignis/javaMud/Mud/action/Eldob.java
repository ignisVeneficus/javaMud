package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Item;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.ReturnType;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Eldob extends Action{

	@Override
	public String getName() {
		return "eldob";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		Holder env = source.getEnvironment();
		if(env==null) {
			return true;
		}
		if(source instanceof Player) {
			if(((Player)source).isGhost()) {
				source.tell("Egy szellemnek nincs semmije");
				return true;
			}
		}
		if(StringUtils.isBlank(paramLine)) {
			source.tell("Mit akarsz eldobni?");
			return true;
		}
		List<String> params = parseCommand(paramLine, 0);
		
		if((params.size() == 1) && (StringUtil.equalsSecoundString("mindent", params.get(0)))){
			Item[] entityList = source.getContains();
			if(entityList.length==0) {
				source.tell("Nincs nálad semmit.");
				return true;
			}
			boolean ok = false;
			for(Entity e:entityList) {
				boolean bt = eldob(env,(Item)e,source);
				if(!bt) {
					e.moveObject(env);
					ok = true;
				}
				ok = ok || bt;
			}
			if(ok) {
				Event e = Event.createSimpleSourceEvent(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas),
						source, "%S mindenét szétszórja.",
						null,
						"Mindened eldobod");
				if(e!=null) {
					e.fire();
				}
				source.updatedRoom();
			}
			return true;
		}
		List<ItemPointer> poiList = parse(params);
		List<Sense> senseList = source.getSenseByType(Defaults.senses);
		Entity target = target(source, poiList, senseList, false);
		if(target == null) {
			source.tell("Nincs olyanod!");
			return true;
		}
		// viselve ellenorzes
		if(eldob(env,(Item)target,source)){
			return true;
		}
		SenseTest t = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
		ArrayList<SenseTest> set = new ArrayList<>();
		set.add(t);
		Event e = Event.createSimpleSourceSubjectEvent(set, source, target,
				"%S eldobja %T.",
				"%T feltűnik.",
				"%S matat valamit",
				null,
				"Eldobod a %T.");
		target.moveObject(env, null, e);
		source.updatedRoom();
		return true;
	}
	/**
	 * A targy tenyleges eldobasanak elokeszitese
	 * @param env ahova dobja
	 * @param itm amit dob
	 * @param source aki dobja
	 * @return true, ha le lett kezelve, es nem kell az altalanos eldobasnak foglalkoznia vele, pl sajat fg, pl nem lehet eldobni mert viseli, stb..
	 */
	private boolean eldob(Holder env, Item itm, Living source) {
		ReturnType type = itm.canDrop();
		// error
		if(type==null) return true;
		
		if(!type.isSuccess()) {
			String str = type.getFailedString();
			if(!StringUtils.isBlank(str)) {
				source.tell(str);
			}
			return true;
		}
		if(itm.hasActionFor(getName())) {
			boolean b = itm.action(getName(), new String[0], source);
			if(b) return true;
		}
		
		return false;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"eldob" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET +"\n" +
				"Eldobod a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " tárgyat a cuccaid közül.\n"+
				"Amit viselsz, vagy fogsz, azt nem tudod eldobni (előtte a "+Defaults.Color_command+"eltesz" +Colorize.RESET + " vagy " +Defaults.Color_command+"levesz" +Colorize.RESET +" paranccsal le kell venned, el kell tenned).\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd eldobni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"eldob tőr 2" + Colorize.RESET +".\n"+
				"Az " + Defaults.Color_command + "eldob mindent" + Colorize.RESET + " paranccsal minden tárgyad, amit nem viselsz, vagy fogsz, eldobod.\n"+
				"Az " + Defaults.Color_command + "eldob "+Colorize.RESET + " " +Defaults.Color_command_arg + "<mennyi>" +Colorize.RESET + " " +Defaults.Color_command_arg +"pénz" + Colorize.RESET + " paranccsal "+ Defaults.Color_command_arg + "<mennyi>" + Colorize.RESET +" pénzt dobsz el.";
	}

}
