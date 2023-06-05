package org.ignis.javaMud.Mud.action;

import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Felkel extends Action {
	@Override
	public String getName() {
		return "felkel";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if(source instanceof Player) {
			Player p = (Player)source;
			if(!p.isResting()) {
				p.tell("Nem is pihensz!");
				return true;
			}
			Holder env = p.getEnvironment();
			if(env!=null) {
				Event ev = Event.createSimpleSourceEvent(Defaults.Sense_Latas, env.getStimulus(Defaults.Sense_Latas), source, "%S felkel a pihenésből.", "", "Felkelsz a pihenésből.");
				if(ev!=null) {
					ev.fire();
				}
			}
			else {
				
			}
			p.setResting(false);
		}
		return true;
	}
	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"felkel" +Colorize.RESET + "\nMegszakítod a pihenésed.\nA "+Defaults.Color_command+"pihen" +Colorize.RESET + " paranccsal ülhetsz le pihenni.";
	}

}
