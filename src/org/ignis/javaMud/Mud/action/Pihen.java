package org.ignis.javaMud.Mud.action;

import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Pihen extends Action {

	@Override
	public String getName() {
		return "pihen";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if(source instanceof Player) {
			Player p = (Player)source;
			if(p.isGhost()) {
				p.tell("Egy szellem nem pihenhet!");
				return true;
			}
			// ellenorizni, hogy harcol-e
			
			if(p.isResting()) {
				p.tell("Már pihensz!");
				return true;
			}
			Holder env = p.getEnvironment();
			if(env!=null) {
				if(env instanceof Room) {
					if(((Room)env).isCantRest()) {
						p.tell("Itt nem pihenhetsz!");
						return true;
					}
				}
				
				Event ev = Event.createSimpleSourceEvent(Defaults.Sense_Latas, env.getStimulus(Defaults.Sense_Latas), source, "%S leül pihenni.", "", "Leülsz pihenni.");
				if(ev!=null) {
					ev.fire();
				}
			}
			else {
				
			}
			p.setResting(true);
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"pihen" +Colorize.RESET + "\nLeülsz pihenni, amikor is gyorsabban gyógyulsz, de semmit sem csinálhatsz.\nA "+Defaults.Color_command+"felkel" +Colorize.RESET + " paranccsal szakíthatod meg.";
	}

}
