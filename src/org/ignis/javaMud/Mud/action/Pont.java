package org.ignis.javaMud.Mud.action;

import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Pont extends Action {

	@Override
	public String getName() {
		return "pont";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if((source!=null)&&(source instanceof Player)) {
			((Player)source).tellStatus();
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"pont" +Colorize.RESET + "\nMegmutatja az értékeidet:\n" +Colorize.C_B_RED+"HP"+
				Colorize.RESET + ": Életerőd: mennyi sebet tudsz elviselni, mielőtt meghalnál.\n"+
				Colorize.C_B_GREEN+"SP"+
				Colorize.RESET + ": Kitartásod: mennyit tudsz gyalogolni, mielőtt elfáradnál.\n"+
				Colorize.C_B_CYAN+"MP"+
				Colorize.RESET + ": Manapontod (ha van): A varázslások során fogy.";				
	}

}
