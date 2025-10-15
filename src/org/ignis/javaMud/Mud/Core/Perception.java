package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.dataholder.Test;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;

/**
 * 
 * @author Csaba Toth
 * Eszleles szamitas
 */
public class Perception {
	static private Logger LOG = LogManager.getLogger(Perception.class);

	public static final int NONE_BELOW = -2;
	public static final int SOME_BELOW = -1;
	public static final int ALL = 0;
	public static final int SOME_ABOVE = 1;
	public static final int NONE_ABOVE = 2;
	
	public static final int TYPE_SOURCE = 0;
	public static final int TYPE_REFLECTION = 1;
	public static final int TYPE_INDIVIDUAL = 2;

	public static final String T_ANY = "any";
	public static final String T_ALL = "all";
	
	
	/**
	 * 
	 * @param sense erzek
	 * @param environment kornyezet
	 * @param stimulus amit tesztelunk
	 * @return
	 */
	public static int test(Sense sense, int environment, int stimulus) {
		LOG.trace("sense: " + sense + " env: "+ environment+ " stimulus: " + stimulus);
		int type = sense.getPerceptionType();
		int m = sense.getMean();
		int v = sense.getValue();
		int act = 0;
		double multiple = 1;
		switch(type) {
		case TYPE_SOURCE:
			act = stimulus;//*2-environment;
			break;
		case TYPE_REFLECTION:
			act = environment;
			multiple = ((double)stimulus)/100.0;
			break;
		case TYPE_INDIVIDUAL:
			act=stimulus;
			break;
		}
		int ret = 0;
		ret = doTest(type, m, v, act, multiple);
		return ret;
	}

	
	static private int doTest(int type, int m, int v, int act,double multiple) {
		double calc =Math.exp(-Math.pow((double)(act*multiple-m),2.0)/Math.pow((double)v, 2.0))*100;
		int ret = 0;
		if(calc < 50) {
			ret = -1;
		}
		if(calc < 20) {
			ret = -2;
		}
		if(act > m) ret *= -1;
		if(((type==TYPE_SOURCE)||(type==TYPE_INDIVIDUAL)) && (ret>0)) {
			ret = 0;
		}
		LOG.trace("type: type: " + type + " act: "+ act+ " mean: " + m + " variance: " + v + " ret: " + calc + " res: "+ ret + " multiple: " + multiple);
		return ret;
	}
	
	/**
	 * 
	 * @param senseList erzekek listaja
	 * @param environment kornyezet
	 * @param stimulus amit vizsgalunk
	 * @return
	 */
	public static int test(List<Sense> senseList, int environment, int stimulus) {
		int result = 3;
		for(Sense s:senseList) {
			int tRes = test(s,environment,stimulus);
			if(tRes==0) return 0;
			if(result > Math.abs(tRes)) {
				result = tRes;
			}
		}
		return result;
	}
	
	
	/**
	 * Ellenorzest vegez, hogy mi ismerheto meg
	 * @param senseList erzekek listaja
	 * @param environment kornyezeti ertekek
	 * @param tests tesztek listaja (cel szamok)
	 * @param type hogy mindegykre vagy barmelyikre kell-e
	 * @return eredmeny
	 */
	public static int test(List<Sense> senseList, Map<String, Integer> environment, List<SenseTest> tests, String type) {
		int result = 3;
		HashMap<String, ArrayList<Sense>> senses = new HashMap<>();
		for(Sense s:senseList) {
			ArrayList<Sense> sl = senses.get(s.getSenseType());
			if(sl==null) {
				sl = new ArrayList<>();
				senses.put(s.getSenseType(), sl);
			}
			sl.add(s);
		}
		for(Test t:tests) {
			String ttype = t.getName();
			int env = environment.get(ttype);
			ArrayList<Sense> sl = senses.get(ttype);
			if(sl!=null) {
				int res = test(sl, env, t.getDifficulty());
				if(T_ANY.equals(type)) {
					if(res==0) return 0;
					if(result > Math.abs(res)) {
						result = res;
					}						
				}
				else {
					if(result< Math.abs(res)) {
						result = res;
					}
				}
			}
		}
		return result;
	}

}
