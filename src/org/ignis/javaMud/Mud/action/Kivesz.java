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

public class Kivesz extends Action {

	@Override
	public String getName() {
		return "kivesz";
	}

	@Override
	public boolean process(String paramList, Living source) {
		Holder env = source.getEnvironment();
		if(env==null) {
			return true;
		}
		if(source instanceof Player) {
			if(((Player)source).isGhost()) {
				source.tell("Egy szellem semmit sem vehet ki sehonnét!");
				return true;
			}
		}
		List<String> params = parseCommand(paramList, 0);
		if(params.size()==0) {
			source.tell("Mit akarsz kiveni?");
			return true;
		}
		List<Sense> senseList = source.getSenseByType(Defaults.senses);
		ItemPointer poi=null;
		List<ItemPointer> boxList = null;
		boolean mindent = false;
		if(StringUtil.equalsSecoundString("mindent", params.get(0))) {
			mindent = true;
			boxList = parse(params.subList(1, params.size()));
		}
		else {
			List<ItemPointer> t = parse(params);
			poi = t.get(0);
			boxList = t.subList(1, t.size());
		}

		if(boxList.size()==0) {
			source.tell("Honnét akarod kivenni?");
			return true;
		}
		Holder box = source;
		boolean inRoom = false;
		Entity t = target(source, boxList, senseList, false);
		if(t==null) {
			inRoom=true;
			t = target(env,boxList,senseList,true);
		}
		if(t==null) {
			source.tell("Nincs itt olyan!");
			return true;
		}
		if(t instanceof Living) {
			source.tell("Élőből nem vehetsz ki semmit!");
			return true;
		}
		if(!(t instanceof Holder)) {
			source.tell("Nem tároló!");
			return true;
		}
		box = (Holder)t;		
		
		if(mindent) {
			List<Entity> entityList = box.getItems(source);
			if(entityList.size()==0) {
				source.tell("Nincs itt semmi.");
				return true;
			}
			Map<String, Integer> environment = env.getStimulus(Defaults.senses);
			int qty = 0;
			for(Entity e:entityList) {
				if(e instanceof Item) {
					if(kivesz((Item)e,source,inRoom,senseList,environment)) {
						e.moveObject(source);
						qty++;
					}
				}
			}
			if(qty==0) {
				source.tell("Nincs itt semmi.");
				return true;
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
		
		Entity target = target(box, poi, senseList, inRoom);
		if(target == null) {
			source.tell("Nincs itt olyan!");
			return true;
		}
		if(target instanceof Living) {
			source.tell("Élőlényt nem vehetsz fel!");
			return true;
		}
		if(kivesz((Item)target,source)) {
			// suly ellenorzes
			SenseTest test = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
			ArrayList<SenseTest> set = new ArrayList<>();
			set.add(test);
			Event e = Event.createSimpleSourceSubjectEvent(set, source, target,
					"%S felveszi %T.",
					"%T eltünik.",
					"%S matat valamit",
					null,
					"Felveszed a %T.");
			if(e!=null) {
				e.fire();
			}
			target.moveObject(source, null, null);
			source.updatedRoom();
		}
		return true;
	}

	private boolean kivesz(Item target,Living source) {
		return true;
	}
	
	private boolean kivesz(Item target,Living source,boolean inRoom,List<Sense> senseList, Map<String, Integer> environment) {
		if(inRoom) {
			int res = checkSense(target, senseList, environment);
			if(res!=Perception.ALL) return false;
		}
		
		return kivesz(target,source);
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"kivesz" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " " + Defaults.Color_command_arg + "<honnét>" + Colorize.RESET +"\n" +
				"Kiveszed a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " tárgyat a "+ Defaults.Color_command_arg + "<honnét>" + Colorize.RESET +" tárolóból.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd kivenni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"kivesz tőr 2 láda" + Colorize.RESET +" vagy " +Defaults.Color_command+"kivesz tőr láda 2" + Colorize.RESET +".\n"+
				"A " + Defaults.Color_command + "kivesz mindent <honnét>" + Colorize.RESET + " paranccsal mindent kiveszel.";
	}

}
