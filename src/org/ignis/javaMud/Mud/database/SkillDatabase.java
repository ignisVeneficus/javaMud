package org.ignis.javaMud.Mud.database;

import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Skill;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="skills")
public class SkillDatabase extends Database<SkillItem> {
	static final public String REG_NAME = "SkillDB";

	@XmlElement(name="item")
	private ArrayList<SkillItem> items;
	
	@Override
	public String getName() {
		return REG_NAME;
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		for(SkillItem item:items) {
			String name = StringUtil.exEkezet(item.getName());
			if(StringUtils.isNotBlank(name)) {
				elements.put(name, item);
			}
		}
	}
	
	public Skill createOne(String name) {
		name = StringUtil.exEkezet(name);
		SkillItem itm =elements.get(name);
		if(itm==null) return null;
		return itm.createOne();
	}
}
