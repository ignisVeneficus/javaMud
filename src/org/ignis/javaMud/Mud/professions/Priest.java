package org.ignis.javaMud.Mud.professions;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Monster;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Priest extends Profession {
	static private Logger LOG = LogManager.getLogger(Priest.class);
	static public final String TYPE="priest";  

	@Override
	public boolean action(String command, String[] param, Living source) {
		return false;
	}

	@Override
	public boolean handle(List<String> line,Monster obj) {
		if(line.size()<2) return false;
		String who = line.get(0);
		
		if(line.size()==2) {
			String sec = StringUtil.exEkezet(line.get(1));
			if("sugo".equalsIgnoreCase(sec) || "segitseg".equalsIgnoreCase(sec)) {
				String force = "mond A "+Defaults.Color_command+"mond feltámaszt" +Colorize.RESET + " parancssal kaphatod vissza a tested, hogyha meghaltál és szellemként jösz ide.";
				obj.force(force);
				// azert, hogy mas sugo-k is lefussanak
				return false;
			}
			if("feltamaszt".equalsIgnoreCase(sec)) {
				String name = StringUtils.substringBefore(who, Player.GHOST_SHORT);
				Entity e = obj.getEnvironment().isPresent(name);
				if(e==null) {
					obj.force("mond Nem talállak fiam.");
					return true;
				}
				if(obj.canNotice(e)!=Perception.ALL) {
					obj.force("mond Nem talállak fiam.");
					return true;
				}
				if(e instanceof Player) {
					Player p = (Player)e;
					if(!p.isGhost()) {
						obj.force("mond Szerencsére nem vagy szellem!");
						return true;
					}
					Event event = Event.createSimpleSourceEvent(Defaults.Sense_Hallas, Defaults.getDefaultIntensity(Defaults.Sense_Hallas), obj, "%S hosszasan imádkozik", "Hosszú imádság hallatszik", "");
					if(event!=null) {
						event.fire();
					}
					p.setGhost(false);
					event = Event.createSimpleSourceEvent(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas), p, "%S szelleme testet ölt", null, "Visszakapod a tested.");
					if(event!=null) {
						event.fire();
					}
					obj.force("mond Visszakaptad a tested. Menj és próbálj meg rá jobban vigyázni.");
					return true;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void init(Map<String, String[]> data) {
	}

}
