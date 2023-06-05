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

public class Megfog extends Action {

	@Override
	public String getName() {
		return "megfog";
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
			source.tell("Mit akarsz megfogni?");
			return true;
		}
		List<String> params = parseCommand(paramLine, 0);

		if((params.size() == 1) && (StringUtil.equalsSecoundString("mindent", params.get(0)))){
			Item[] entityList = source.getContains();
			int qty = 0;
			boolean ok = false;
			for(Entity e:entityList) {
				//
				if((e instanceof Weapon)&&(!((Weapon)e).isUsed())) {
					qty++;
					boolean bt = megfog((Weapon)e, source);
					ok = ok || bt;
				}
			}
			if(qty==0) {
				source.tell("Nem tudsz megfogni semmit.");
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
			source.tell("Nincs olyanod!");
		}
		return megfog((Weapon)target, source);
	}

	/**
	 * A targy tenyleges megfogas
	 * @param itm amit megfog
	 * @param source aki dobja
	 * @return true, ha le lett kezelve, es nem kell az altalanos eldobasnak foglalkoznia vele, pl sajat fg, pl nem lehet eldobni mert viseli, stb..
	 */
	private boolean megfog(Weapon itm, Living source) {
		ReturnType type = itm.hold();
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
		return "Használat: "+Defaults.Color_command+"megfog" +Colorize.RESET + " " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET +"\n" +
				"Megfogod (kézbe veszed) a " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + " tárgyat ha nálad van. Ezt követően fogni fogod, vagyis tudod harcban használni.\n"+
				"Ha valamiből több is van, és nem az elsőt szeretnéd megfogni, akkor egy sorszámmal utahatsz rá, pl: "+Defaults.Color_command+"megfog tőr 2" + Colorize.RESET +".\n"+
				"A " + Defaults.Color_command + "megfog mindent" + Colorize.RESET + " paranccsal minden tárgyad, amit tudsz (és van szabad kezed), megfogod.";
	}


}
