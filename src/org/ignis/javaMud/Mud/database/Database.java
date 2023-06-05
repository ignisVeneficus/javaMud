package org.ignis.javaMud.Mud.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.handlers.Handler;

@XmlSeeAlso({ SkillDatabase.class,RaceDatabase.class })

@XmlAccessorType(XmlAccessType.FIELD)
abstract public class Database<T extends DatabaseItem<?>> implements Handler{
	@XmlTransient
	static private Logger LOG = LogManager.getLogger(Database.class);
	
	private String sugo;
	
	@XmlTransient
	protected HashMap<String, T> elements;

	public Database(){
		elements = new HashMap<>();
	}
	

	@Override
	public void init(Engine e) {
	}

	@Override
	public void dest() {
	}
	public String getSugo(String[] parts) {
		if(parts.length == 0) {
			return sugo;
		}
		String key = StringUtil.exEkezet(parts[0]);
		if(StringUtils.isBlank(key)) {
			return sugo;
		}
		T item = elements.get(key);
		if(item==null) {
			return "Nincs olyan!";
		}
		return item.getSugo(Arrays.copyOfRange(parts, 1, parts.length));
	}
	abstract public String getName();
	
	public T getItem(String name) {
		return elements.get(name);
	}
}
