package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.ignis.javaMud.Mud.StringUtil;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BonusHandler {

	/**
	 * Neve
	 */
	@XmlAttribute
	private String name;
	/**
	 * Erteke
	 */
	@XmlAttribute
	private int value;
	
	/**
	 * Bonuszok
	 */
	private List<Bonus> bonusList; 
	
	public BonusHandler() {
		bonusList = Collections.synchronizedList(new ArrayList<Bonus>());
	}
	
	public BonusHandler(String name, int value) {
		this.name = name;
		this.value = value;
		bonusList = Collections.synchronizedList(new ArrayList<Bonus>());
	}
	public void addBonus(List<Bonus> list) {
		for(Bonus b:list) {
			addBonus(b);
		}
	}

	public void addBonus(Bonus b) {
		String n = b.getName();
		String type = b.getType();
		if(StringUtil.equalsSecoundString(getType(), type) && StringUtil.equalsString(getName(), n)) {
			if(b.getId()!=null) {
				for(Bonus tb:bonusList) {
					if(StringUtil.equalsString(tb.getId(), b.getId())) {
						return;
					}
				}
			}
			synchronized (bonusList) {
				bonusList.add(b);
			}
		}
	}
	public void removeBonus(Bonus b) {
		synchronized (bonusList) {
			bonusList.remove(b);
		}
		
	}
	public void removeBonus(String bonusId) {
		synchronized (bonusList) {
			for(Bonus tb:bonusList) {
				if(StringUtil.equalsString(tb.getId(), bonusId)) {
					bonusList.remove(tb);
				}
			}
		}
	}
	public void removeBonusBySource(java.lang.Object source) {
		synchronized (bonusList) {
			for(Bonus tb:bonusList) {
				if(tb.getSource() == source) {
					bonusList.remove(tb);
				}
			}
		}
	}
	
	public List<Bonus> getBonuses(){
		Bonus[] array;
		synchronized (bonusList) {
			array = new Bonus[bonusList.size()];
			bonusList.toArray(array);
		}
		return Arrays.asList(array);
	}
	
	abstract public String getType();
	public String getName() {
		return name;
	}
	public int getBaseValue() {
		return value;
	}
	public int getValue() {
		int ret = value;
		Bonus[] bonusArray;
		synchronized (bonusList) {
			bonusArray = new Bonus[bonusList.size()];
			bonusArray = bonusList.toArray(bonusArray);
		}
		for(Bonus b:bonusArray) {
			ret+= b.getValue();
		}
		return ret;
	}

	public void setBaseValue(int value) {
		this.value = value;
	}
}
