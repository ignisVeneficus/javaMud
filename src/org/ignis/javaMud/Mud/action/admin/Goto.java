package org.ignis.javaMud.Mud.action.admin;

import java.util.List;

import org.ignis.javaMud.Mud.Core.Living;

public class Goto extends Admin {

	@Override
	public String getName() {
		return "goto";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean process(String paramsList, Living source) {
		if(!super.process(paramsList, source)) return false;
		
		List<String> params = parseCommand(paramsList, 1);
		source.moveObject(params.get(0), null, null);
		return true;
	}

}
