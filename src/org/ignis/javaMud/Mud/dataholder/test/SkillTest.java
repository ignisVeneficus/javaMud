package org.ignis.javaMud.Mud.dataholder.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Skill;
import org.ignis.javaMud.Mud.dataholder.Test;

public class SkillTest extends Test {
	static private Logger LOG = LogManager.getLogger(SkillTest.class);

	public SkillTest(String name, int difficulty) {
		super(Test.T_SKILL,name,difficulty);
	}
	public SkillTest() {
		super();
		this.setType(T_SKILL);
	}
	@Override
	public boolean doTest(Living obj) {
		int res = doSkillTest(obj);
		return Skill.isSuccess(res);
	}
	
	public int doSkillTest(Living obj) {
		Skill sk = obj.getSkill(getName());
		if(sk==null) return -1;
		return sk.test(getDifficulty());
	}

}
