package org.ignis.javaMud.Mud.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.dataholder.ObjectRef;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;

public class HolderUtils {
	static private Logger LOG = LogManager.getLogger(HolderUtils.class);
	
	static public List<String> orderResultList(List<String> list){
		ArrayList<String> middle = new ArrayList<>();
		String last = null;
		int qty =0;
		for(String str:list) {
			if(str==null) {
				LOG.fatal("Valakinek nincs rovid neve!");
			}
			else if(!str.equals(last)){
				if(last!=null) {
					String strQty = Defaults.numberToText(qty);
					middle.add(strQty + last);
				}
				last = str;
				qty = 0;
			}
			qty++;
		}
		if(last!=null) {
			String strQty = Defaults.numberToText(qty);
			middle.add(strQty + last);
		}
		return middle;
	}
	static public String getObjectDescription(Holder env,Living source, List<Entity> list, String some) {
		Map<String, Integer> environment = env.getStimulus(Defaults.senses);
		ArrayList<Sense> senseList = source.getSenseByType(Defaults.senses);
		return getObjectDescription(senseList, environment, list, some);
	}
	static public String getObjectDescription(ArrayList<Sense> senseList, Map<String, Integer> environment, List<Entity> list, String some) {
		
		List<String> startlist = getObjects(senseList,environment, list, some);
		List<String> middle = orderResultList(startlist);
		return StringUtil.listToString(middle);
	}	
	/**
	 * Osszeszedi egy objektum listabol, hogy mit vehet eszre (string lista)
	 * @param obj ki figyel
	 * @param list bemeneti objektum lista
	 * @param some hogy ha nem tudja a nevet, mit adjon vissza
	 * @return lista az objektumokrol
	 */
	static public List<String> getObjects(ArrayList<Sense> senseList, Map<String, Integer> environment,List<Entity> list,String some) {
		ArrayList<String> objlist = new ArrayList<>();
		if(LOG.isTraceEnabled()) {
			LOG.trace("env qty: " + environment.size());
			for(Entry<String,Integer> itm:environment.entrySet()) {
				LOG.trace("env: " + itm.getKey() + "->" + itm.getValue());
			}
		}
		for(Entity itm:list) {
			ArrayList<SenseTest> tests = itm.getPerceptibility(Defaults.senses);
			int res = Perception.test(senseList, environment, tests,Perception.T_ANY);
			LOG.trace("Check: " + itm.getFullObjectName() + " result: " + res);
			switch(res) {
			case Perception.ALL:
				objlist.add(itm.getShortNameString());
				break;
			case Perception.SOME_ABOVE:
			case Perception.SOME_BELOW:
				objlist.add(some);
				break;
			}
		}
		Collections.sort(objlist, StringUtil.simpleCollator);
		return objlist;
	}
	
	static public List<Entity> getObjects(Holder env,Living obj, List<Entity> list) {
		ArrayList<Entity> objlist = new ArrayList<>();
		ArrayList<Sense> senseList = obj.getSenseByType(Defaults.senses);
		Map<String, Integer> environment = env.getStimulus(Defaults.senses);
		if(LOG.isTraceEnabled()) {
			for(Entry<String,Integer> itm:environment.entrySet()) {
				LOG.trace("env: " + itm.getKey() + "->" + itm.getValue());
			}
		}
		for(Entity itm:list) {
			ArrayList<SenseTest> tests = itm.getPerceptibility(Defaults.senses);
			int res = Perception.test(senseList, environment, tests,Perception.T_ANY);
			LOG.trace(obj.getFullObjectName() + " check: " + itm.getFullObjectName() + " result: " + res);
			if ( res==Perception.ALL) {
				objlist.add(itm);
				break;
			}
		}
		return objlist;
	}
	
	/**
	 * Rekurzive objektum referencia betolto
	 * 
	 * @param list betoltendo OBJref lista
	 * @param e engine, a betolteshez
	 * @param holder environment, ahova tenni kellene
	 * @param needEvent kell-e eventet megjeleniteni
	 */
	static public void handleObjRef(List<ObjectRef> list, Engine e, Holder holder,boolean needEvent) {
		LOG.trace("handle " + list.size() + " item");
		for (ObjectRef ref : list) {
			String name = e.compileRef(ref.getName());
			LOG.trace(name);
			int qty = ref.getQty();
			for (int i = 0; i < qty; i++) {
				Entity obj = e.load(name);
				if (obj != null) {
					Event evt = needEvent?obj.getAppearEvent():null;
					obj.moveObject(holder, null, evt);
					if((obj instanceof Holder)&&(ref.getContent()!=null) && (ref.getContent().size()>0)) {
						handleObjRef(ref.getContent(), e, (Holder)obj,needEvent);
					}
				}
			}
		}
	}
	static public int count(Entity[] list,String name) {
		int ret = 0;
		for(Entity e:list) {
			if(name.equals(e.getFullObjectName())) {
				ret++;
			}
		}
		return ret;
	}
	
	static public void MergeStimulus(Map<String, ArrayList<Stimulus>> into, Map<String, ArrayList<Stimulus>> from) {
		for(String key:into.keySet()) {
			ArrayList<Stimulus> one = into.get(key);
			ArrayList<Stimulus> two = from.get(key);
			if((one!=null) && (two!=null)) {
				one.addAll(two);
			}
		}
	}
}
