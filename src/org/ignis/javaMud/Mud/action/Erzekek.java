package org.ignis.javaMud.Mud.action;

import java.util.List;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Erzekek extends Action {

	@Override
	public String getName() {
		return "érzékek";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if((source==null)|| !(source instanceof Player)) {
			return true;
		}
		StringBuffer buff = new StringBuffer();
		buff.append("Az érzékeid:");
		Player p = (Player)source;
		for(String s:Defaults.senses) {
			String color = colorize(s);
			List<Sense> sl= p.getSenseByType(s);
			for(Sense se:sl) {
				String name = se.getName();
				if(name.length()>20) {
					name = name.substring(0, 19)+"…";
				}
				buff.append("\n");
				buff.append(String.format("%1$20s", name));
				buff.append(" [");
				buff.append(color);
				double from = Math.max(((double)se.getMean()-(double)se.getValue())/100,0);
				double to = Math.min(((double)se.getMean()+(double)se.getValue())/100,1);
				buff.append(StringUtil.drawLine(from, to, 80-24));
				buff.append(Colorize.RESET);
				buff.append("]");
			}
		}
		p.tell(buff.toString());
		return true;
	}
	private String colorize(String type) {
		if(type.equalsIgnoreCase(Defaults.Sense_Hallas)) return Defaults.Color_hallas;
		if(type.equalsIgnoreCase(Defaults.Sense_Latas)) return Defaults.Color_latas;
		if(type.equalsIgnoreCase(Defaults.Sense_Magia)) return Defaults.Color_magia;
		if(type.equalsIgnoreCase(Defaults.Sense_Szaglas)) return Defaults.Color_szaglas;
		return "";
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"érzékek" +Colorize.RESET + "\nMegmutatja az érzékeid éleségét.\n";				
	}

}
