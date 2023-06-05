package org.ignis.javaMud.Mud.dataholder.monster;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ignis.javaMud.utils.xml.MapAdapterArray;

@XmlAccessorType(XmlAccessType.FIELD)
public class Profession {
	@XmlAttribute(name="type")
	private String name;
	@XmlJavaTypeAdapter(MapAdapterArray.class)
	private Map<String, String[]> properties;
	
	public String getName() {
		return name;
	}
	public Map<String, String[]> getProperties() {
		return properties;
	}
}
