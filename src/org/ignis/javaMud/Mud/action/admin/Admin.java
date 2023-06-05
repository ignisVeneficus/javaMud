package org.ignis.javaMud.Mud.action.admin;

import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.action.Action;

public abstract class Admin extends Action {

	@Override
	public boolean process(String paramList, Living source) {
		//check admin
		return true;
	}

}
