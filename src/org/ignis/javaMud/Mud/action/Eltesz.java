package org.ignis.javaMud.Mud.action;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Item;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.ReturnType;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.Core.Weapon;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Eltesz extends Action {

	@Override
	public String getName() {
		return "eltesz";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if(source instanceof Player) {
			if(((Player)source).isGhost()) {
				source.tell("Egy szellemnek nincs semmije");
				return true;
			}
		}
		if(StringUtils.isBlank(paramLine)) {
			source.tell("Mit akarsz eltenni?");
			return true;
		}
		List<String> params = parseCommand(paramLine, 0);

		if((params.size() == 1) && (StringUtil.equalsSecoundString("mindent", params.get(0)))){
			Item[] entityList = source.getContains();
			int qty = 0;
			boolean ok = false;
			for(Entity e:entityList) {
				if((e instanceof Weapon)&&(((Weapon)e).isUsed())) {
					qty++;
					boolean bt = eltesz((Weapon)e, source);
					ok = ok || bt;
				}
			}
			if(qty==0) {
				source.tell("Nem fogsz semmit.");
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
		if(!(target instanceof Weapon) || (!((Weapon)target).isUsed())) {
			source.tell("Nem fogod!");
			return true;
		}
		return eltesz((Weapon)target, source);
	}

	/**
	 * A targy tenyleges eldobasanak elokeszitese
	 * @param env ahova dobja
	 * @param itm amit dob
	 * @param source aki dobja
	 * @return true, ha le lett kezelve, es nem kell az altalanos eldobasnak foglalkoznia vele, pl sajat fg, pl nem lehet eldobni mert viseli, stb..
	 */
	private boolean eltesz(Weapon itm, Living source) {
		ReturnType type = itm.unHold();
		// error
		if(type==null) return true;
		
		if(!type.isSuccess()) {
			String str = type.getFailedString();
			if(!StringUtils.isBlank(str)) {
				source.tell(str);
			}
			return true;
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"eltesz" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET +"\n" +
				"Elteszed a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " tárgyat ha fogod. Ezt követően nem fogod, vagyis nem tudod harcban használni.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd eltenni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"eltesz tőr 2" + Colorize.RESET +".\n"+
				"Az " + Defaults.Color_command + "eltesz mindent" + Colorize.RESET + " paranccsal minden tárgyad, amit fogsz, elteszed.";
	}


}
