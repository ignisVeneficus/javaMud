package org.ignis.javaMud.Mud.Core;

import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.utils.Defaults;
/**
 * Tulajdonsagok
 * @author Ignis
 *
 */
public class Stat extends BonusHandler implements Comparable<Stat>{
	
	public Stat() {
		super();
	}

	
	@Override
	public int compareTo(Stat s) {
		return StringUtil.simpleCollator.compare(getName(),s.getName());
	}


	@Override
	public String getType() {
		return Defaults.Name_Stat;
	}

}
