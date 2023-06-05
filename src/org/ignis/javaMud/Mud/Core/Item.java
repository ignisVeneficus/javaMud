package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;
import org.ignis.javaMud.utils.CoreTools;
import org.ignis.javaMud.utils.xml.DiceIntegerAdapter;
import org.ignis.javaMud.utils.xml.JsMapAdapter;
/**
 * Altalanos targyak, es a fegyverek, pancelok, stb ose
 * Minden ami nem szoba es eloleny
 * 
 * @author Ignis
 *
 */
@XmlRootElement(name ="Item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item extends Entity implements HeartBeatListener{
	static private Logger LOG = LogManager.getLogger(Item.class);

	/**
	 * Suly 0.1 kg-ban
	 */
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer weight;

	/**
	 * ar, alap.
	 */
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer price;

	/**
	 * Action map, az actiont (action elso szava) mappeli ossze a js fuggvenyekkel
	 */
	@XmlElement(name="action")
	@XmlJavaTypeAdapter(JsMapAdapter.class)
	private HashMap<String,String> jsAction;
	
	
	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/
	
	@XmlTransient
	private boolean hasHeartBeat = false;

	
	
	@Override
	protected void init() {
		super.init();
		if (weight==null) {
			weight = 0;
		}
		if (price==null) {
			price = 0;
		}
		if(hasJs()) {
			String[] jsA = new String[jsAction.keySet().size()];
			jsA = jsAction.keySet().toArray(jsA);
			for(String s:jsA) {
				if(!hasJsFunction(jsAction.get(s))) {
					jsAction.remove(s);
				}
			}
			if(hasJsFunction("heartBeat")) {
				CoreTools.registerHeartBeat(engine, this);
				hasHeartBeat = true;
			}
		}
	}

	public Item() {
		super();
		jsAction = new HashMap<>();
	}

	@Override
	public boolean action(String command, String[] param, Living source) {
		command = StringUtil.exEkezet(command);
		LOG.trace("command: " + command + " size: " + param.length + " living: " + source.getFullObjectName());
		if(hasJs()) {
			String action = jsAction.get(command);
			if(action!=null) {
				try {
					Invocable invocable = evalJs();
					java.lang.Object funcResult = invocable.invokeFunction(action,this,source,param);
			        if(funcResult instanceof Boolean) {
			        	if(((Boolean)funcResult)==true) return true;
			        }
				} catch (Exception e) {
					LOG.catching(Level.FATAL, e);
					source.tell("Valami rossz történt!");
				}
			}
		}
		return false;
	}

	@Override
	public int getPerceptibility(String type) {
		LOG.warn("Default ertekekket hasznalva!");
		return Defaults.getDefaultIntensity(type);
	}

	@Override
	protected void _destr() {
		if(hasHeartBeat)
			CoreTools.unRegisterHeartBeat(engine, this);
		super._destr();
	}

	@Override
	protected boolean canDestroy() {
		return true;
	}

	@Override
	public ArrayList<SenseTest> getPerceptibility(Set<String> type) {
		String[] types = new String[type.size()];
		types = type.toArray(types);
		return getPerceptibility(types);
	}
	@Override
	public ArrayList<SenseTest> getPerceptibility(String[] type) {
		ArrayList<SenseTest> ret = new ArrayList<>();
		for (String st : type) {
			int i = getPerceptibility(st);
			SenseTest t = new SenseTest(st, i);
			ret.add(t);
		}
		return ret;
	}

	public ArrayList<Stimulus> getAdditionalLatas(int intensity){
		return null;
	}
	
	@Override
	public String getDescription(Living obj, boolean longDescr) {
		// jatekos mashonnet is megkapja az informacio
		//if(obj instanceof Player) return null;
		ArrayList<Stimulus> latasList = new ArrayList<>();
		ArrayList<Stimulus> hallasList = new ArrayList<>();
		ArrayList<Stimulus> szaglasList = new ArrayList<>();
		ArrayList<Stimulus> magiaList = new ArrayList<>();
		
		Set<String> astronomyStatus = getEnvironment().getEnvironmentStatus();
		if(LOG.isTraceEnabled()) {
			LOG.trace("Ast status: " + String.join(";", astronomyStatus));
		}
		
		java.util.Map<String,ArrayList<Stimulus>> map = collectAllStimulus(Defaults.senses,astronomyStatus);
		latasList = map.get(Defaults.Sense_Latas);
		hallasList = map.get(Defaults.Sense_Hallas);
		szaglasList = map.get(Defaults.Sense_Szaglas);
		magiaList = map.get(Defaults.Sense_Magia);
	
		Map<String, Integer> ints = getTopEnvironment().getStimulus(Defaults.senses,astronomyStatus);

		//Latashoz hozzakerul egyeb
		int latasMax = 0;
		for(Stimulus s: latasList) {
			latasMax=Math.max(latasMax, s.getIntensity());
		}
		ArrayList<Stimulus> latasAdd = getAdditionalLatas(latasMax);
		if(latasAdd!=null) {
			latasList.addAll(latasAdd);
		}
	
		
		StringBuffer buff = new StringBuffer();
		buff.append("\n");
		String latas = getDescription(obj, Defaults.Sense_Latas, latasList, longDescr, ints.getOrDefault(Defaults.Sense_Latas, 0));
		LOG.trace("latas: " + latas);
		if(StringUtils.isNotBlank(latas)) {
			buff.append(Defaults.Color_latas);
			buff.append(latas);
			buff.append(Colorize.RESET);
			buff.append("\n");
		}
		String hallas = getDescription(obj, Defaults.Sense_Hallas, hallasList, longDescr, ints.getOrDefault(Defaults.Sense_Hallas, 0));
		LOG.trace("hallas: " + hallas);
		if(StringUtils.isNotBlank(hallas)) {
			buff.append(Defaults.Color_hallas);
			buff.append(hallas);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		String szaglas = getDescription(obj, Defaults.Sense_Szaglas, szaglasList, longDescr, ints.getOrDefault(Defaults.Sense_Szaglas, 0));
		LOG.trace("szaglas: " + szaglas);
		if(StringUtils.isNotBlank(szaglas)) {
			buff.append(Defaults.Color_szaglas);
			buff.append(szaglas);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		String magia = getDescription(obj, Defaults.Sense_Magia, magiaList, longDescr, ints.getOrDefault(Defaults.Sense_Magia, 0));
		LOG.trace("magia: " + magia);
		if(StringUtils.isNotBlank(magia)) {
			buff.append(Defaults.Color_magia);
			buff.append(magia);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		return buff.toString();
		
	}

	
	public boolean hasActionFor(String action) {
		return jsAction.containsKey(action);
	}
	
	public ReturnType canDrop() {
		return ReturnType.success();
	}

	@Override
	public void tick() {
		if(hasJs() && hasJsFunction("heartBeat")) {
			callJsFunction("heartBeat");
		}
		
	}
	
}
