package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Item;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Felvesz extends Action {

	@Override
	public String getName() {
		return "felvesz";
	}

	@Override
	public boolean process(String paramList, Living source) {
		Holder env = source.getEnvironment();
		if(env==null) {
			return true;
		}
		if(source instanceof Player) {
			if(((Player)source).isGhost()) {
				source.tell("Egy szellem semmit sem vehet fel!");
				return true;
			}
		}
		List<String> params = parseCommand(paramList, 0);
		List<ItemPointer> poiList = parse(params).subList(0, 1);
		
		if(poiList.size()==0) {
			source.tell("Mit akarsz felvenni?");
			return true;
		}
		List<Sense> senseList = source.getSenseByType(Defaults.senses);
		
		// mit kell felvenni
		ItemPointer poi = poiList.get(0);
		String name=poi.getName();
		if(StringUtil.equalsSecoundString("mindent", name)){
			List<Entity> entityList = env.getItems(source);
			Map<String, Integer> environment = env.getStimulus(Defaults.senses);
			if(entityList.size()==0) {
				source.tell("Nincs itt semmi.");
				return true;
			}
			for(Entity e:entityList) {
				boolean success = felvesz((Item)e,source,senseList,environment);
				if(success)
					e.moveObject(source);
			}
			Event e = Event.createSimpleSourceEvent(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas),
					source, "%S összeszed mindent.",
					null,
					"Összeszedsz mindent ami mozditható.");
			if(e!=null) {
				e.fire();
			}
			source.updatedRoom();
			return true;
		}
		
		Entity target = target(env, poiList, senseList, true);
		if(target == null) {
			source.tell("Nincs itt olyan!");
			return true;
		}
		if(target instanceof Living) {
			source.tell("Élőlényt nem vehetsz fel!");
			return true;
		}
		boolean success = felvesz((Item)target,source);
		if(success) {
			SenseTest t = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
			ArrayList<SenseTest> set = new ArrayList<>();
			set.add(t);
			Event e = Event.createSimpleSourceSubjectEvent(set, source, target,
					"%S felveszi %T.",
					"%T eltünik.",
					"%S matat valamit",
					null,
					"Felveszed a %T.");
			target.moveObject(source, e, null);
			source.updatedRoom();
		}
		return true;
	}
	
	private boolean felvesz(Item itm, Living source) {
		// ellenorzesek (suly, hely, stb)

		return true;
		
	}
	private boolean felvesz(Item itm, Living source,List<Sense> senseList, Map<String, Integer> environment) {
		int res = checkSense(itm, senseList, environment);
		if(res != Perception.ALL) return false;
		return felvesz(itm,source);
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"felvesz" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET +"\n" +
				"Felveszed a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " tárgyat a földről.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd felvenni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"felvesz tőr 2" + Colorize.RESET +".\n"+
				"A " + Defaults.Color_command + "felvesz mindent" + Colorize.RESET + " paranccsal mindent felveszel.";
	}

}
