package org.ignis.javaMud.Mud.action;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Food;
import org.ignis.javaMud.Mud.Core.Item;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Eszik extends Action {

	@Override
	public String getName() {
		return "eszik";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if(source instanceof Player) {
			if(((Player)source).isGhost()) {
				source.tell("Egy szellem nem ehet semmit.");
				return true;
			}
		}
		List<String> params = parseCommand(paramLine, 0);
		if(params.size()==0) {
			source.tell("Mit akarsz megenni?");
			return true;
		}
		if(source.getHPLost()==0) {
			source.tell("Tele vagy, most nem tudsz enni!");
			return true;
		}
		
		if((params.size() == 1) && (StringUtil.equalsSecoundString("mindent", params.get(0)))){
			Item[] entityList = source.getContains();
			if(entityList.length==0) {
				source.tell("Nincs nálad semmit.");
				return true;
			}
			boolean ok = false;
			for(Entity en:entityList) {
				// ellenorzes
				if(en instanceof Food) {
					Food f = (Food)en;
					if(source.getHPLost()>=f.getHp()) {
						ok = ok || eat(f,source);
					}
				}
			}
			if(ok) {
				source.tellStatus();
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
		if(target instanceof Food) {
			Food f = (Food)target;
			if(eat(f, source)) {
				source.tellStatus();
			}
		}
		else {
			source.tell("Nem ehető!");
			return true;
			
		}
		return true;
	}

	private boolean eat(Food f, Living source) {
		if(f.hasActionFor(getName())) {
			boolean b = f.action(getName(), new String[0], source);
			if(b) return true;
		}
		Event e = Event.createSimpleSourceEvent(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas),
				source, "%S evett valamit.",
				null,
				"Megetted a "+f.getShortName()+"-ed.");
		if(e!=null) {
			e.fire();
		}
		source.addHP(f.getHp());
		if(StringUtils.isNotBlank(f.getRemainder())) {
			Entity rem = f.getObject(f.getRemainder());
			if(rem!=null) {
				rem.moveObject(source);
			}
		}
		f.destr();
		return true;
	}
	
	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"eszik" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET +"\n" +
				"Megeszed a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " nálad lévő ételt.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd megenni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"eszik alma 2" + Colorize.RESET +".\n"+
				"A " + Defaults.Color_command + "felvesz mindent" + Colorize.RESET + " paranccsal mindent megeszel.";
	}

}
