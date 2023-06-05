package org.ignis.javaMud.Mud.action;

import java.util.Collections;
import java.util.List;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Skill;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Kepzettsegeim extends Action {

	@Override
	public String getName() {
		return "kepzettsegeim";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		List<Skill> skills = source.getAllSkill();
		Collections.sort(skills);
		StringBuffer buff = new StringBuffer();
		//           0        1         2         3         4         5         6         7
		//           1234567890123456789012345678901234567890123456789012345678901234567890123456789
		buff.append("Képzettség           Akt.  Alap  Mod.  \n");
		buff.append("═══════════════════╪═════╪═════╪═════╪═════════════════════════════════════════\n");
		for(Skill s:skills) {
			int l = s.getValue();
			int b = s.getBaseValue();
			int bo = l-b;
			buff.append(Colorize.C_B_GREEN);
			String name = s.getName();
			if(name.length()>20) {
				name = name.substring(0, 19)+"…";
			}
			buff.append(String.format("%-20s", name));
			buff.append(Colorize.RESET);
			buff.append(" ");
			buff.append(String.format("%4d  %4d  ", l,b));
			if(bo<0) {
				buff.append(Colorize.C_B_RED);
			}
			if(bo==0) {
				buff.append(Colorize.C_YELLOW);
			}
			if(bo>0) {
				buff.append(Colorize.C_B_GREEN);
			}
			buff.append(String.format("%4d",bo));
			buff.append(Colorize.RESET);
			buff.append("  [");
			double scale= l*0.01;
			if(scale<0) scale=0;
			if(scale>1) scale=1;
			buff.append(Colorize.C_B_GREEN);
			buff.append(StringUtil.drawLine(scale,38));
			buff.append(Colorize.RESET);
			buff.append("]\n");
		}
		source.tell(buff.toString());
		
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"képzettségeim" +Colorize.RESET + "\nKilistázza az imsert képzettségeidet\n";
	}

}
