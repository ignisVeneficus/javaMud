package org.ignis.javaMud.Mud.professions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfessionFactory {
	static private Logger LOG = LogManager.getLogger(ProfessionFactory.class);
	static public final Profession create(String type) {
		LOG.debug(type);
		if(Priest.TYPE.equalsIgnoreCase(type)) {
			return new Priest();
		}
		return null;
	}
}
