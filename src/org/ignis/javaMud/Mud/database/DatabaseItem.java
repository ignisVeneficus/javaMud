package org.ignis.javaMud.Mud.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
abstract public class DatabaseItem<T> {
	@XmlAttribute
	private String name;
	@XmlElement
	private String sugo;
	
	abstract public T createOne();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSugo(String[] parts) {
		return sugo;
	}
	
}
