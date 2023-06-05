package org.ignis.javaMud.Mud.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="racess")
public class RaceDatabase extends Database<RaceItem> {
	static final public String REG_NAME = "RaceDB";

	@Override
	public String getName() {
		return REG_NAME;
	}

}
