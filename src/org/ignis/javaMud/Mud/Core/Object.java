package org.ignis.javaMud.Mud.Core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.xml.MapAdapterArray;

/**
 * Ose mindennek Elvileg 2 leszarmazasi aga van - Entity -> targyak (dobozok,
 * fegyverek, pancelok), + Living -> elok (szornyek, NPC, jatekosok) - Room ->
 * szobak
 * 
 * Objektumok elnevezese: path/nev$uniqueId
 */
@XmlSeeAlso({ Room.class, OutlandsRoom.class, Player.class, Monster.class, Item.class, RottableContainer.class,
		Container.class, Weapon.class, Food.class })
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Object {
	static private Logger LOG = LogManager.getLogger(Object.class);
	static private Logger TODO = LogManager.getLogger("TODO");

	public static final int STATUS_NEW = 0;
	public static final int STATUS_OK = 1;
	public static final int STATUS_DESTROYED = 2;

	private String longName;
	@XmlJavaTypeAdapter(MapAdapterArray.class)
	private Map<String, String[]> properties;


	/**
	 * leirasok
	 */
	@XmlElement(name = "stimulus")
	private ArrayList<Stimulus> stimulus;

	@XmlElement(name = "TODO")
	private String todoString;

	@XmlElement(name = "js")
	private String JsRaw;
	@XmlElement(name = "jsfile")
	private String JsRawFile;

	/*
	 * ********************************************* localis vatozok
	 * 
	 *********************************************/
	/**
	 * Objektum statusz: uj: inicializalas alatt ok, hasznalhato dest: megsemmittes
	 * alatt
	 */
	@XmlTransient
	protected int status;
	/**
	 * Mud kezelo
	 */
	@XmlTransient
	public Engine engine;
	/**
	 * egyedi azonosito
	 */
	@XmlTransient
	private String uniqueId;
	@XmlTransient
	private String fileName;

	@XmlTransient
	private CompiledScript js;

	@XmlTransient
	private Map<String, Boolean> jsFunctions;

	public synchronized void setEngine(Engine engine) {
		if (this.engine == null)
			this.engine = engine;
	}

	public Object() {
		status = STATUS_NEW;
		stimulus = new ArrayList<>();
		jsFunctions = new HashedMap<>();
	}

	public final void initObject(String name, String uniqueId, Engine engine) {
		synchronized (this) {
			if (status != STATUS_NEW)
				return;
			this.uniqueId = uniqueId;
			this.fileName = name;
			properties = Collections.synchronizedMap(new HashedMap<String, String[]>());
			setEngine(engine);
			init();
			
			if(hasJsFunction("init")) {
				callJsFunction("init");
			}

			status = STATUS_OK;
		}
	}

	/**
	 * Osztaly definit init, celja elvegezni azokat a beallitasokat, amiket az xml
	 * felolvasa utan kellenek.
	 */
	protected void init() {
		if (StringUtils.isNotBlank(todoString)) {
			TODO.info(fileName + ": " + todoString);
		}
		if (StringUtils.isNotBlank(JsRaw)) {
			try {
				js = engine.compileScript(JsRaw);
			} catch (Exception e) {
				LOG.catching(Level.FATAL, e);
			}
		} else {
			if (StringUtils.isNotBlank(JsRawFile)) {
				try {
					String javascript = engine.getContent(JsRawFile);
					js = engine.compileScript(javascript);
				} catch (Exception e) {
					LOG.catching(Level.FATAL, e);
				}
			}
		}
	}

	public String getFullObjectName() {
		return fileName + "$" + uniqueId;
	}


	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	protected void _destr() {
	}

	protected abstract boolean canDestroy();

	public final boolean _destrObject() {
		synchronized (this) {
			if (status == STATUS_NEW)
				return false;
			if (status == STATUS_DESTROYED)
				return false;
			boolean check = canDestroy();
			if (check) {
				status = STATUS_DESTROYED;
				_destr();
			}
			return check;
		}
	}

	public int getStatus() {
		return status;
	}

	public boolean callName(String name) {
		if (this.fileName.equalsIgnoreCase(name))
			return true;
		return false;
	}
	
	public Map<String, String[]> getProperties() {
		return properties;
	}

	public void setProperties(ConcurrentHashMap<String, String[]> properties) {
		this.properties.putAll(properties);
	}

	public void setProperty(String key, String value) {
		properties.put(key, new String[] { value });
	}

	public String[] getProperties(String key) {
		return properties.get(key);
	}

	public String getProperty(String key) {
		String[] values = properties.get(key);
		if (values != null) {
			return StringUtils.join(values, "|");
		}
		return null;
	}


	public String getName() {
		return fileName;
	}

	abstract public String getDescription(Living obj, boolean longDescr);

	@SuppressWarnings("unchecked")
	public void set(String field, String value) {
		LOG.trace("method: set" + StringUtils.capitalize(field));
		String fnName = "set" + StringUtils.capitalize(field);
		try {
			Class<? extends Object> cl = this.getClass();
			Method fn = null;
			try {
				fn = cl.getDeclaredMethod(fnName, String.class);
			} catch (Exception e) {
			}
			while ((fn == null) && ((cl = (Class<? extends Object>) cl.getSuperclass()) != null)) {
				try {
					fn = cl.getDeclaredMethod(fnName, String.class);
				} catch (Exception e) {
				}
			}
			if (fn != null) {
				try {
					LOG.trace("found: " + cl.toString());
					LOG.trace("found: " + fn.toString());
					fn.invoke(this, value);
				} catch (Exception e) {
					LOG.error("Error to call " + fn + " with: " + value);
				}
			} else {
				LOG.error("No method found for " + this.getClass() + " for field: " + fnName);

			}
		} catch (Exception e) {
			LOG.error("Error for call the setter: " + this.getClass() + " for field: " + fnName);

		}
	}

	public Handler getHandler(String what) {
		if (engine == null)
			return null;
		return engine.getHandler(what);
	}

	public ArrayList<Stimulus> getStimulus() {
		return stimulus;
	}
	/*
	 * public void setStimulus(ArrayList<Stimulus> stimulus) { this.stimulus =
	 * stimulus; }
	 */

	public ArrayList<Stimulus> collectAllStimulus(String senseType, Set<String> astronomyStatus) {
		Set<String> objStatus = getObjectStatus();
		objStatus.addAll(astronomyStatus);
		ArrayList<Stimulus> list = new ArrayList<>();
		for (Stimulus s : stimulus) {
			if (testStimulus(s, senseType, objStatus)) {
				list.add(s);
			}
		}
		return list;
	}
	
	public Set<String> getObjectStatus(){
		Set<String> ret = new HashSet<>();
		if(hasJsFunction("getObjectStatus")) {
			try {
				Invocable invocable = evalJs();
				java.lang.Object funcResult = invocable.invokeFunction("getObjectStatus",this);
		        if(funcResult instanceof String) {
		        	ret.addAll(Arrays.asList(funcResult.toString().split(" ")));
		        }
			} catch (Exception e) {
				LOG.catching(Level.FATAL, e);
			}
		}
		return ret;
	}

	public Map<String, ArrayList<Stimulus>> collectAllStimulus(Set<String> senseTypes, Set<String> astronomyStatus) {
		Set<String> objStatus = getObjectStatus();
		objStatus.addAll(astronomyStatus);
		HashMap<String, ArrayList<Stimulus>> ret = new HashMap<>();
		for (String st : senseTypes) {
			ArrayList<Stimulus> list = new ArrayList<>();
			ret.put(st, list);
		}
		for (Stimulus s : stimulus) {
			if (testStimulus(s, senseTypes, objStatus)) {
				String type = s.getType();
				ret.get(type).add(s);
			}
		}
		return ret;
	}
	public java.util.Map<String, ArrayList<Stimulus>> collectAllStimulusInside(Set<String> types,
			Set<String> astronomyStatus){
		return collectAllStimulus(types,astronomyStatus);
	}
	public ArrayList<Stimulus> collectAllStimulusInside(String type,
			Set<String> astronomyStatus){
		return collectAllStimulus(type,astronomyStatus);
	}

	private boolean testStimulus(Stimulus s, String senseType, Set<String> astronomyStatus) {
		HashSet<String> set = new HashSet<>();
		set.add(senseType);
		return testStimulus(s, set, astronomyStatus);
	}

	/**
	 * ellenorzi, hogy egy adott bemeneti stimulus megfelel-e a tipus es kornyezeti
	 * allapotoknak
	 * 
	 * @param s               bemeneti stimulus
	 * @param senseType       tipus
	 * @param astronomyStatus kornyezeti allapotok
	 * @return true ha megfelel
	 */
	private boolean testStimulus(Stimulus s, Set<String> senseTypes, Set<String> astronomyStatus) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Stimulus: " + String.join(";", s.getType()) + " Status: " + String.join(";", astronomyStatus)
					+ " Cond: " + s.getCondition());
		}
		String cond = StringUtil.exEkezet(s.getCondition());
		if ((StringUtils.isBlank(cond) || astronomyStatus.contains(cond))) {
			String type = s.getType();
			if (senseTypes.contains(StringUtil.exEkezet(type))) {
				return true;
			}
		}
		return false;
	}

	public Invocable evalJs() {
		try {
			js.eval();
			return (Invocable) js.getEngine();
		} catch (Exception e) {
			LOG.catching(Level.FATAL, e);
		}
		return null;
	}

	public boolean hasJs() {
		return js != null;
	}


	public Entity getObject(String obj) {
		return engine.load(obj);
	}

	public void destr() {
		engine.destrObject(this);
	}

	private static boolean isJsFunction(CompiledScript js, String name) throws ScriptException {
		LOG.trace("Test js function: "+name);
		ScriptEngine scengine = js.getEngine();
		js.eval();
		String test = "typeof " + name + " === 'function' ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE";
		Boolean b = (Boolean) scengine.eval(test);
		LOG.trace("Test js function: "+name + " result: " + b);
		return b;
	}

	public boolean hasJsFunction(String name) {
		Boolean b = jsFunctions.get(name);
		if (b != null)
			return b;
		// nem teszteltuk elotte
		if (js == null) {
			b = false;
		} else {
			try {
				b = isJsFunction(js, name);
			} catch (Exception e) {
				b = false;
			}
		}
		jsFunctions.put(name, b);
		return b;
	}
	
	public java.lang.Object callJsFunction(String fnName) {
		try {
			Invocable invocable = evalJs();
			return invocable.invokeFunction(fnName,this);
		} catch (Exception e) {
			LOG.catching(Level.FATAL, e);
		}
		return null;
	}

	public java.lang.Object checkAndCallJsFunction(String fnName){
		if(StringUtils.isNotBlank(fnName)) {
			if(hasJsFunction(fnName)) {
				return callJsFunction(fnName);
			}
		}		
		return null;
	}
	
	public String getDescription(Living obj, String type, ArrayList<Stimulus> descrList, boolean longDescr, int env) {
		LOG.trace("Default intensity for: " + type + ": "+ env);
		ArrayList<Sense> sense = obj.getSenseByType(type);
		if(sense.size()==0) return null;
		if(LOG.isTraceEnabled()) {
			for(Sense s:sense) {
				LOG.trace("Sense: " + s.toString());
			}
		}
		boolean shortDescr = !longDescr;
		int min = 2;
		int max = -2;
		boolean hasNormal = false;
		StringBuffer buff=new StringBuffer();
		for(Stimulus s:descrList) {
			LOG.trace("Stimulus: " + s.toString());
			int res = Perception.test(sense, env,s.getIntensity());
			min = Math.min(min, res);
			max = Math.max(max, res);
			LOG.trace("res:" + res);
			if(res==Perception.ALL) {
				String descr = processSense(s,shortDescr);
				buff.append(descr);
				hasNormal = true;
			}
			if((res > Perception.ALL) &&( (sense.get(0).getPerceptionType()==Perception.TYPE_SOURCE) || (sense.get(0).getPerceptionType()==Perception.TYPE_INDIVIDUAL))) {
				String descr = processSense(s,shortDescr);
				buff.append(descr);
				buff.append("\n");
				hasNormal = true;
			}
		}
		if(!hasNormal) {
			if(StringUtil.equalsString(type, Defaults.Sense_Latas)) {
				String defstr = Defaults.getDefaultDescriptionForSense(type, max);
				if(StringUtils.isNotBlank(defstr)) {
					buff.append(defstr);
					buff.append("\n");
				}
			}
			else {
				buff.append("\n");
			}
		}
		return buff.toString();
	}
	
	
	public String processSense(Stimulus s, boolean shortDescr) {
		return shortDescr?s.getShortDescr(this):s.getDescr(this);
	}

	/*
	 * helper fuggvenyek, foleg a js elereshez nem lehetnek static mert a js nem eri
	 * el
	 */
	public SenseTest createDefaultSenseTest(String sense) {
		String st = StringUtil.exEkezet(sense);
		if (!Defaults.senses.contains(st))
			return null;
		int intenseity = Defaults.getDefaultIntensity(st);
		return new SenseTest(st, intenseity);
	}

	public Event createSimpleSourceEvent(SenseTest[] tests, Entity source, String txtEnvSource, String txtEnv,
			String txtSource) {
		List<SenseTest> testList = Arrays.asList(tests);
		return Event.createSimpleSourceEvent(testList, source, txtEnvSource, txtEnv, txtSource);
	}

	public Event createSimpleSourceEvent(List<SenseTest> tests, Entity source, String txtEnvSource, String txtEnv,
			String txtSource) {
		return Event.createSimpleSourceEvent(tests, source, txtEnvSource, txtEnv, txtSource);
	}
	public Event createSimpleSourceSubjectEvent(SenseTest[] tests, Entity source, Entity subject, String txtEnvSourceTarget, String txtEnvTarget, String txtEnvSource, String txtEnv, String txtSource) {
		List<SenseTest> testList = Arrays.asList(tests);
		return Event.createSimpleSourceSubjectEvent(testList, source, subject, txtEnvSourceTarget, txtEnvTarget, txtEnvSource, txtEnv, txtSource);
	}
	public Event createSimpleSourceSubjectEvent(List<SenseTest> testList, Entity source, Entity subject, String txtEnvSourceTarget, String txtEnvTarget, String txtEnvSource, String txtEnv, String txtSource) {
		return Event.createSimpleSourceSubjectEvent(testList, source, subject, txtEnvSourceTarget, txtEnvTarget, txtEnvSource, txtEnv, txtSource);
	}

	public void addSenseTest(List<SenseTest> tests, String sense, int amouth) {
		for (SenseTest st : tests) {
			if (StringUtil.equalsString(st.getName(), sense)) {
				st.changeDifficulty(amouth);
			}
		}
	}
	public boolean equalsString(String one,String two) {
		return StringUtil.equalsString(one, two);
	}

	
}
