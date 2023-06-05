package org.ignis.javaMud.Mud.action;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Mond extends Action {
	static final public Pattern mondPattern = Pattern.compile("^(.*?) mondja: (.*)");
	
	@Override
	public String getName() {
		return "mond";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		Holder env = source.getEnvironment();
		if(env==null) {
			return true;
		}
		if(StringUtils.isBlank(paramLine)) {
			source.tell("Mit akarsz mondani?");
			return true;
		}
		Event e = Event.createSimpleSourceEvent(Defaults.Sense_Hallas, Defaults.getDefaultIntensity(Defaults.Sense_Hallas), source, "%S mondja: " +paramLine, "Az hallatszik: "+paramLine, "Azt mondod: " +paramLine);
		if(e!=null) {
			e.fire();
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"mond" +Colorize.RESET +" " + Defaults.Color_command_arg + "<mit>" + Colorize.RESET + 
				"\nA "+ Defaults.Color_command_arg + "<mit>" + Colorize.RESET+"-et mondod a szobába, amit mindenki hallhat." ;
	}

}
