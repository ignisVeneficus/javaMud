package org.ignis.javaMud.Mud.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Holder;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Perception;
import org.ignis.javaMud.Mud.Core.Sense;
import org.ignis.javaMud.Mud.utils.Defaults;
/**
 * 
 * @author Ignis
 *
 * Parancs ososztaly
 *
 */
abstract public class Action {
	static private Logger LOG = LogManager.getLogger(Action.class);

	/**
	 * A parancs kivalto szava
	 * @return a kivalto szo
	 */
	abstract public String getName();
	/**
	 * parancs feldolgozasa
	 * @param paramLine a parameterek stringkent
	 * @param source a living, aki kiadta
	 * @return true ha vegrehajtotta (akar hibaval is), true eseten nem keres tobb parancsot
	 */
	abstract public boolean process(String paramLine, Living source);
	
	/**
	 * Help (sugo) szoveg. ez jelenik meg a sugo parancs hatasasa
	 * @return szoveg
	 */
	abstract public String getHelp();
	
	protected static List<ItemPointer> parse(List<String> params){
		ArrayList<ItemPointer> ret = new ArrayList<>();
		int qty = 0;
		while(qty<params.size()) {
			boolean hasNumber = false;
			int number = 1;
			if(qty<params.size()-1) {
				try {
					number = Integer.parseInt(params.get(qty+1));
					hasNumber = true;
				}
				catch(Exception e) {
				}
			}
			ItemPointer poi = new ItemPointer(params.get(qty), number);
			qty++;
			if(hasNumber) {
				qty++;
			}
			ret.add(poi);
		}
		return ret;
	}
	
	/**
	 * Celzas. Megkeresi a celpontot az environment terben. Minden esetben vizsgalja, hogy az adott illeto (senseList) mit vehet eszre. Kezeli a szamossagot is.
	 * @param env Ter, amiben keres
	 * @param list Amit keres. Az otolso 1 v 2 elem a tarolo, amiben keresi, <tarolo> [<sorszam>], ha megtalalta  atarolot, akkor megy lejjebb
	 * @param senseList az erzekei annak aki keres
	 * @param checkSense kell-e az erzek ellenorzes (pl sajat zsebeibe nem kell)
	 * @return a megtalalt celpont
	 */
	protected static Entity target(Holder env, List<ItemPointer> poiList, List<Sense> senseList,boolean checkSense) {
		if(poiList == null) return null;
		if(poiList.size()==0) return null;
		
		ItemPointer poi = poiList.get(poiList.size()-1);
		Entity ret = null;
		String what = poi.getName();
		
		List<ItemPointer> newList = poiList.subList(0, poiList.size()-1);
		
		
		// atgondolni.. pl magiaerzekeles?
		Map<String, Integer> environment = env.getStimulus(Defaults.senses);
		int qty = 0;
		while( (ret= env.isPresent(what,ret)) !=null) {
			int res = Perception.ALL;
			LOG.trace("Found: " + ret.getFullObjectName());
			if(checkSense) {
				res = checkSense(ret, senseList, environment);
			}
			else {
				LOG.trace("noCheck");
			}
			if((!checkSense) ||(res == Perception.ALL)) {
				qty++;
				if(qty==poi.getNumber()) {
				
					if(newList.size() == 0) {
						return ret;
					}
					else {
						// elo zsebeiben nem kotoraszunk.
						// amennyiben szukseges, akkor a elore hivjuk meg az elso kort (sajat cucc)
						if(ret instanceof Living) {
							return null;
						}
						if(ret instanceof Holder) {
							return target((Holder)ret,newList,senseList,checkSense);
						}
					}
					// van celpont, csak rossz a tobbi celzas (nem holder, vagy jatekos akarni)
					return null;
				}
			}
		}		
		return null;
	}
	
	protected static Entity target(Holder env, ItemPointer poi, List<Sense> senseList,boolean checkSense) {
		ArrayList<ItemPointer> poiList = new ArrayList<>();
		poiList.add(poi);
		return target(env, poiList, senseList, checkSense);
	}

	
	static public List<String> parseCommand(String input,int needed) {
		List<String> result = new ArrayList<String>();
		int start = 0;
		boolean inQuotes = false;
		for (int current = 0; current < input.length(); current++) {
		    if (input.charAt(current) == '\"') inQuotes = !inQuotes; // toggle state
		    else if (input.charAt(current) == ' ' && !inQuotes) {
		        result.add(input.substring(start, current).replaceAll("^\"|\"$", ""));
		        start = current + 1;
		        if((needed!=0)&&(result.size()==needed)) {
		        	break;
		        }
		    }
		}
		result.add(input.substring(start));
		return result;
	}
	static public String getCommand(String input) {
		List<String> res = parseCommand(input, 1);
		if(res.size()>0) {
			return res.get(0);
		}
		return "";
	}
	
	static public int checkSense(Entity obj,List<Sense> senseList,Map<String, Integer> environment) {
		int res = 3;
		for(String se:Defaults.senses) {
			int diff = obj.getPerceptibility(se);
			int tRes = Perception.test(senseList, environment.get(se), diff);
			if(tRes==0) {
				res =0;
				break;
			}
			if(res > Math.abs(tRes)) {
				res = tRes;
			}
			LOG.trace(se+ " siker: " + res);
		}
		return res;
	}
}
