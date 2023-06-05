package org.ignis.javaMud.Mud.dataholder.test;

import java.util.ArrayList;

import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.Test;

public class SenseTest extends Test {
	public SenseTest(String name, int difficulty) {
		super(Test.T_SENSE,name,difficulty);
	}
	public SenseTest() {
		super();
		setType(T_SENSE);
	}
	@Override
	public boolean doTest(Living obj) {
		if(obj==null) return false;
		ArrayList<Sense> senseList = obj.getSenseByType(getName());
		if((senseList==null) || (senseList.size()==0)) return false;
		Object env = obj.getEnvironment();
		if((env==null) || !(env instanceof Room)) return false;
		Room r = (Room)env;
		int environment = r.getStimulus(getName());
		int ret = Perception.test(senseList, environment, getDifficulty());
		return (ret == 0);
	}
}
