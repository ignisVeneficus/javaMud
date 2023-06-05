package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.handlers.ActionHandler;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;

public class Sugo extends Action {

	@Override
	public String getName() {
		return "súgó";
	}

	@Override
	public boolean process(String paramLine, Living source) {
		if(source == null) return true;
		
		if(StringUtils.isBlank(paramLine)) {
			source.tell(getHelp());
			return true;
		}
		Handler h = source.getHandler(ActionHandler.REG_NAME);
		if((h ==null)|| !(h instanceof ActionHandler)) {
			return true;
		}
		List<String> params = parseCommand(paramLine, 0);
		
		ActionHandler ah = (ActionHandler)h;
		// a parancsok listaja
		if(StringUtil.equalsSecoundString("parancsok",params.get(0))){
			StringBuffer buff = new StringBuffer();
			buff.append("Minden parancsról információt lehet kapni a ");
			buff.append(Defaults.Color_command);
			buff.append("súgó");
			buff.append(Colorize.RESET);
			buff.append(" ");
			buff.append(Defaults.Color_command);
			buff.append("parancs");
			buff.append(Colorize.RESET);
			buff.append(" ");
			buff.append(Defaults.Color_command_arg);
			buff.append("<parancs>");
			buff.append(Colorize.RESET);
			buff.append(" paranccsal, ahol a ");
			buff.append(Defaults.Color_command_arg);
			buff.append("<parancs>");
			buff.append(Colorize.RESET);	
			buff.append(" a parancs neve, emire kiváncsi vagy.\n");
			buff.append("Általános parancsok:");
			Action[] list = ah.getAllAction();
			ArrayList<String> commandList = new ArrayList<String>();
			for(Action a:list) {
				commandList.add(a.getName());
			}
			StringUtil.writeList(buff,commandList,Defaults.Color_command_arg,Colorize.RESET);
			
			source.tell(buff.toString());
			return true;
		}
		
		// egy parancs
		if(StringUtil.equalsSecoundString("parancs",params.get(0))){
			if(params.size()<2) {
				source.tell("Hibás kérdés");
				return true;
			}
			String command = params.get(1);
			Action a = ah.getAction(command);
			if(a!=null) {
				source.tell(a.getHelp());
				return true;
			}
			
			// egyeb parancsok
			
			source.tell("Hibás kérdés");
			return true;
			
		}
		source.tell("Hibás kérdés");
		return true;
	}

	@Override
	public String getHelp() {
		return "Használat: "+Defaults.Color_command+"súgó" +Colorize.RESET + "\nSegítséget ír ki.\n"+
				"A "+Defaults.Color_command+"súgó parancsok" + Colorize.RESET +": parancsokat nézheted meg.\n"+
				"A "+Defaults.Color_command+"súgó fajok" + Colorize.RESET +": a fajokat listázza.\n"+
				"A "+Defaults.Color_command+"súgó osztályok" + Colorize.RESET +": a választható karakterosztályok.\n"+
				"A "+Defaults.Color_command+"súgó képességek" + Colorize.RESET +": a karakter képességeinek listája.\n"+
				"A "+Defaults.Color_command+"súgó képzettségek" + Colorize.RESET +": a felvehető képességek.\n"
				;
	}

}
