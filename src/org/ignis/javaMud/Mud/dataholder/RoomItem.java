package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
/**
 * Cucc, ami megnezheto a szobaban, de nem object
 * @author Csaba Toth
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomItem {
	static private Logger LOG = LogManager.getLogger(RoomItem.class);
	/**
	 * Id halmaz, amivel hivatkozni lehet ra
	 */
	@XmlElement(name="name")
	private HashSet<InflectionWord> ids;
	/**
	 * leirasok, "condition"-al sulyosbitva
	 */
	@XmlElement(name="descr")
	private ArrayList<ConditionDescr> descrs;

	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/

	/**
	 * a nevek osszeszedve ID-kra
	 */
	@XmlTransient
	private HashMap<String,InflectionWord> words;
	
	public RoomItem() {
		words = new HashMap<>();
		ids = new HashSet<>();
	}
	public void setIds(HashSet<InflectionWord> ids) {
		this.ids = ids;
	}
	public ArrayList<ConditionDescr> getDescrs() {
		return descrs;
	}
	public void setDescrs(ArrayList<ConditionDescr> descrs) {
		this.descrs = descrs;
	}
	public boolean isIn(String word) {
		if(StringUtils.isBlank(word)) return false;
		return words.containsKey(word);
	}
	
	public String getTargy(String word) {
		InflectionWord w = words.get(word);
		if(w!=null) return w.getTargy();
		return "";
	}
	public String getDescription(String word, Set<String> condition) {
		for(ConditionDescr cd:descrs) {
			if(StringUtils.isBlank(cd.getCondition()) || (condition.contains(cd.getCondition()))) {
				String descr = cd.getDescr();
				if(descr.contains("%I")) {
					return descr.replaceAll("\\%I", word);
				}
				return descr;
			}
		}
		return null;
	}
	void afterUnmarshal(Unmarshaller u, Object parent) {
		LOG.trace("INIT: unmashal: "+ids.size());
		for(InflectionWord w:this.ids) {
			String word = StringUtil.exEkezet(w.getWord());
			LOG.trace("INIT: add: " + word);
			words.put(word, w);
		}
	}
}
