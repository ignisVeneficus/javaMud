package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.script.Invocable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.data.AreaEffect;
import org.ignis.javaMud.Mud.dataholder.Exit;
import org.ignis.javaMud.Mud.dataholder.ObjectRef;
import org.ignis.javaMud.Mud.dataholder.RandomObject;
import org.ignis.javaMud.Mud.dataholder.RoomDescription;
import org.ignis.javaMud.Mud.dataholder.RoomItem;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.cartography.Map;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.deamon.Astronomy;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.Mud.utils.HolderUtils;
import org.ignis.javaMud.utils.Colorize;
import org.ignis.javaMud.utils.CoreTools;
import org.ignis.javaMud.utils.xml.JsMapAdapter;

/**
 * Szoba, amiben targyak (items) es elolenyek (living) lehet
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="Room")
public class Room extends Object implements Holder, HeartBeatListener, Singleton{
	
	@XmlTransient
	static private Logger LOG = LogManager.getLogger(Room.class);

	/**
	 * alap iranyok, mind a 8. 
	 */
	@XmlTransient
	private static final String[] DEF_IRANYOK= {"észak","északkelet","kelet","délkelet","dél","délnyugat","nyugat","északnyugat"};
	
	/**
	 * nyitott szoba-e (latszik-e az eg)
	 */
	@XmlAttribute
	private boolean open;
	/**
	 * Szoba kijaratai
	 */
	@XmlElement(name="exit")
	private ArrayList<Exit> exits;
	
	/**
	 * szobahoz tartozo objektumok, de csak mint path, meg fel kell olvasni oket
	 */
	@XmlElement(name="objRef")
	private ArrayList<ObjectRef> objRefs;
	/**
	 * szobahoz tartozo random objektumok, de csak mint path, meg fel kell olvasni oket
	 */
	@XmlElement(name="randomObjRef")
	private ArrayList<RandomObject> randomObjRefs;
	
	/**
	 * Erkezesi szoveg a szobaban levoknek
	 */
	@XmlElement(name="entryTextRoom")
	private String txtEntryEnvSource;
	/**
	 * Erkezesi szoveg a felhasznalonak
	 */
	@XmlElement(name="entryText")
	private String txtEntrySource;
	/**
	 * A szoba piszkalhato leirasai. Ezeket meg lehet nezni
	 */
	@XmlElement(name="item")
	private ArrayList<RoomItem> items;
	/**
	 * terkep leiro a szobahoz
	 */
	@XmlAttribute(name="mapfile")
	private String mapFile;
	/**
	 * True eseten nem lehet a szobaban pihenni (mert pl uszni kell, stb)
	 */
	@XmlAttribute(name="noRest")
	private boolean cantRest;
	
	/**
	 * Action map, a actiont (action elso szava) mappeli ossze a js fuggvenyekkel
	 */
	@XmlElement(name="action")
	@XmlJavaTypeAdapter(JsMapAdapter.class)
	private HashMap<String,String> jsAction;
	/**
	 * Javascript az erkezes esemenyhez
	 */
	@XmlElement(name="entryEvent")
	private String jsEntryEvent;
	
	
	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/
	/**
	 * Szoba tartalma
	 */
	@XmlTransient
	private List<Entity> contains; 
	/**
	 * egbolt
	 */
	@XmlTransient
	private Astronomy astronomy;
	/**
	 * Teruletre hato efektusok, uszasz, ho, stb
	 */
	@XmlTransient
	private List<AreaEffect> areaEffects;
	/**
	 * Terkep
	 */
	@XmlTransient
	private Map map;
	
	public Room() {
		contains = Collections.synchronizedList(new ArrayList<Entity>());
		exits = new ArrayList<>();
		items = new ArrayList<>();
		areaEffects = Collections.synchronizedList(new ArrayList<AreaEffect>());
		objRefs = new ArrayList<>();
		jsAction = new HashMap<>();
	}
	@Override
	protected void init() {
		super.init();
		for(Exit exit:exits) {
			if(StringUtils.isNotBlank(exit.getLeaveFunc())) {
				if(!hasJsFunction(exit.getLeaveFunc())) {
					exit.setLeaveFunc(null);
				}
			}
		}
		Handler h =engine.getHandler(Astronomy.REG_NAME);
		LOG.trace("astronomy: " + h);
		if((h!=null)&&(h instanceof Astronomy)){
			astronomy = (Astronomy)h;
		}
		LOG.trace("isOpen: " + isOpen());
		if((isOpen())&&(astronomy!=null)) {
			astronomy.registerRoom(this);
		}
		CoreTools.registerHeartBeat(engine, this);
		map = generateMap();
		
		if(hasJs()) {
			String[] jsA = new String[jsAction.keySet().size()];
			jsA = jsAction.keySet().toArray(jsA);
			for(String s:jsA) {
				if(!hasJsFunction(jsAction.get(s))) {
					jsAction.remove(s);
				}
			}
		}
		
		reset();
		
	}
	/**
	 * Az illetot megprobalja az adott iranyba mozgatni.
	 * mindig true-t ad vissza ha az illeto latja az adott kijaratot, fuggetlenul attol hogy sikerult-e kimennie
	 * akkor is true-t ad vissza ha a command a 8 egtaj egyike
	 * 
	 * @param command merre akar lelepni
	 * @param source ki
	 * @return a parancsnak vege van-e (ha van kijarat, de nem tudott kimenni, akkor is true)
	 */
	private boolean tryToExit(String command,Living source) {
		for(Exit exit:exits) {
			if(StringUtil.equalsSecoundString(command,exit.getDirection())) {
				// tud-e mozogni
				if(!source.canMove()) return true;

				// eszleli-e a kijaratot
				
				// van-e blokkolo leny/targy
				ArrayList<String> blockers = exit.getBlokkers();
				if(blockers!=null) {
					for(String n:blockers) {
						Entity blocker = isPresent(n);
						if(blocker!=null){
							/* atirni targyasra 
							String txtEnvSource="";
							String txtEnv="";
							String txtSource="";
							Event evn = Event.createSimpleSourceEvent("latas", Defaults.getDefaultIntensity("latas"), source, txtEnvSource, txtEnv, txtSource);
							
							*/
							return true;
						}
					}
				}
				
				
				
				// van-e eleg SP-je, vagy faradt
				int def = Defaults.getDefaultSPLost();
				double rm = exit.getSpLost();
				double lm = source.getSPMultipleForMoving();
				double mgt = source.getMGT()+1;
				int spLost = (int)Math.round(mgt*def*rm*lm);
				int sp = source.getActSP();
				if(sp<spLost) {
					Set<String> stringList = new HashSet<>();
					stringList.add(Defaults.Sense_Latas);
					stringList.add(Defaults.Sense_Hallas);
					
					Event evn = Event.createSimpleSourceEvent(source.getPerceptibility(stringList), source, "%S megpróbál "+exit.getDirection()+" felé távozni, de majd összeesik a fáradságtól.", "", "Megpróbálsz "+exit.getDirection()+" felé távozni, de majd összeesel a fáradságtól.");
					evn.fire();
					return true;
				}
				
				int res = 0;
				res = handleOutFunc(exit,source,spLost);
				// nem mehet
				if(res==-1) {
					return false;
				}
				// mindent intez a fuggveny
				if(res == 1) {
					return true;
				}
				//standard kilepes
				if(res==0) {
				// kilepesi fg lefuttatasa
					Room newPlace = getDest(exit);
					if(newPlace==null) {
						source.tell("Valami rossz történt!");
						return true;
					}
					// belepesi fg lefuttatasa
					
					// tavozasi esemeny
					
					Event evn = getleaveEvent(exit, source);
					// mozgatas
					source.moveObject((Room)newPlace, evn, null);
					// sp levonas
					if(source.hasMaterialForm())
						source.addSP(-1*spLost);
					return true;
				}
				else {
					LOG.fatal("Invalid leave function return: " + this.getFullObjectName() + " -> " +exit.getDirection());
				}
			}
		}
		for(String str:DEF_IRANYOK) {
			if(StringUtil.equalsSecoundString(command, str)) {
				source.tell("Arra nem mehetsz!");
				return true;
			}
		}
		return false;
		
	}
	protected int handleOutFunc(Exit exit, Living source, int spLost) {
		if(StringUtils.isNoneEmpty(exit.getLeaveFunc())) {
			try {
				Invocable invocable = evalJs();
				Integer it = (Integer)invocable.invokeFunction(exit.getLeaveFunc(),this,exit,source,spLost);
				if(it!=null) {
					return it;
				}
			} catch (Exception e) {
				LOG.catching(Level.FATAL, e);
			}
		}
		return 0;
	}
	private Event getleaveEvent(Exit e,Living source) {
		String txtEnv="";
		String txtEnvSource;
		String txtSource;
		if(StringUtils.isNotBlank(e.getLeaveTxtEnvSource())) {
			txtEnvSource = e.getLeaveTxtEnvSource();
		}
		else {
			txtEnvSource="%S " + e.getDirection() + " felé távozott.";
		}
		if(StringUtils.isNotBlank(e.getLeaveTxtSource())) {
			txtSource = e.getLeaveTxtSource();
		}
		else {
			txtSource= e.getDirection() + " felé távoztál.";
		}
		return Event.createSimpleSourceEvent(source.getAllPerceptibility(), source, txtEnvSource, txtEnv, txtSource);
	}
	
	protected Room getDest(Exit e) {
		Singleton newPlace = engine.getOrLoad(e.getDestination());
		
		// hibakezeles az uj osztaly felolvasa eseten
		if(newPlace == null) {
			LOG.error(getFullObjectName()+" szoba " + e.getDirection() + " kijarata nem letezik!");
			return null;
		}
		if(!(newPlace instanceof Room)) {
			LOG.error(getFullObjectName()+" szoba " + e.getDirection() + " kijarata nem szoba!");
			return null;
		}
		return (Room)newPlace;
	}
	/**
	 * Nez fuggveny
	 * @param what mit nez
	 * @param source ki nez
	 * @return true ha van lathato dolog
	 */
	public boolean look(String  what,Living source) {
			//idk
			Set<String> astronomyStates = astronomy.getAllStates();
			for(RoomItem itm:items) {
				LOG.trace("item: " + what + " ->" + itm.toString() ); 
				if(itm.isIn(what)) {
					LOG.trace("found: " + itm.toString());
					String targy = itm.getTargy(what);
					Event evn = Event.createSimpleSourceEvent(Defaults.Sense_Latas, getStimulus(Defaults.Sense_Latas), source, "%S tüzetesen szemügyre veszi " + targy +".", "", "Tüzetesen szemügyre veszed " + targy + ".");
					evn.fire();
					String descr = itm.getDescription(what,astronomyStates);
					LOG.trace("item descr: " + descr);
					if(StringUtils.isNotBlank(descr)) {
						source.tell(descr);
					}
					return true;
				}
			}
			// utolso az eg
			// atirni roomItem-re
			Set<String> sky = astronomy.getLook();
			if(sky.contains(what)) {
				if(isOpen()) {
					Event evn = Event.createSimpleSourceEvent(Defaults.Sense_Latas, getStimulus(Defaults.Sense_Latas), source, "%S megvizsgálja az eget.", "", "Megvizsgálod az eget.");
					evn.fire();
					String descr = astronomy.getDescription(what);
					if(StringUtils.isNotBlank(descr)) {
						source.tell(descr);
					}
				}
				else {
					source.tell("Innen bentről nem látod.");
				}
				return true;
			}
		return false;
	}
	/**
	 * parancskezeles
	 */

	public boolean action(String command, String[] line, Living source) {
		command = StringUtil.exEkezet(command);
		LOG.trace("command: " + command + " size: " + line.length + " living: " + source.getFullObjectName());
		// kijarat
		if(tryToExit(command, source)) {
			return true;
		}
		// javascript akciok
		if(hasJs()) {
			String action = jsAction.get(command);
			if(action!=null) {
				try {
					/*
					Bindings bindings = js.getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
					bindings.put("_THIS", this);
					bindings.put("_OBJ", source);
					bindings.put("_PARAM", line);
					*/
					Invocable invocable = evalJs();
					java.lang.Object funcResult = invocable.invokeFunction(action,this,source,line);
			        if(funcResult instanceof Boolean) {
			        	if(((Boolean)funcResult)==true) return true;
			        }
				} catch (Exception e) {
					LOG.catching(Level.FATAL, e);
					source.tell("Valami rossz történt!");
				}
			}
		}
		// szornyek akcioi
		ArrayList<Entity> livList = new ArrayList<>();
		Entity[] eList = getContains();
		for(Entity itm:eList) {
			if(itm instanceof Living) {
				if(itm!=source) livList.add(itm);
			}
		}
		if(livList.size()>0) {
			List<Entity> lst = HolderUtils.getObjects(this, source, livList);
			for(Entity itm:lst) {
				if(itm.action(command, line, source)) {
					return true;
				}
			}
		}
		
		// targyak?
		
		return false;
	}
	@Override
	public void tick() {
		if(status!=Object.STATUS_OK) return;
		ThreadContext.push("room: " + getFullObjectName());
		AreaEffect[] effects;
		synchronized (areaEffects) {
			effects = new AreaEffect[areaEffects.size()];
			effects = areaEffects.toArray(effects);
		}
		for(AreaEffect ae:effects) {
			boolean res = ae.tick(getContains());
			LOG.trace("AreaEffect: " + ae.getName() + " result: " + res);
			if(res) areaEffects.remove(ae);
		}
		ThreadContext.pop();
	}
	@Override
	public int getStimulus(String type) {
		Set<String> astronomyStatus = astronomy.getAllStates();
		return getStimulus(type, astronomyStatus);
	}
	@Override
	public int getStimulus(String type,Set<String> astronomyStatus) {
		if(StringUtils.isBlank(type)) return 0;
		int ret = 0;
		type = StringUtil.exEkezet(type);
		if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,type)) {
			if((astronomy!=null) && (isOpen())) {
				ret = astronomy.getLight();
			}
		}
		// osszeszedni a szobaban levo cuccokat is, fenygomb, buzbomba stb..
		ArrayList<Stimulus> actStiom = collectAllStimulus(type, astronomyStatus);
		if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,type)) {
			return Stimulus.getRefStimulus(actStiom, ret);
		}
		else {
			return Stimulus.getOwnStimulus(actStiom);
		}
	
	}
	@Override
	public HashMap<String,Integer> getStimulus(Set<String> types){
		Set<String> astronomyStatus = astronomy.getAllStates();
		return getStimulus(types, astronomyStatus);
	}
	@Override
	public HashMap<String,Integer> getStimulus(Set<String> types,Set<String> astronomyStatus){
		java.util.Map<String,ArrayList<Stimulus>> actStims = collectAllStimulusInside(types, astronomyStatus);
		int sky = 0;
		if((astronomy!=null) && (isOpen())) {
			sky = astronomy.getLight();
		}
		HashMap<String, Integer> ret = new HashMap<>();
		for(String key:types) {
			ArrayList<Stimulus> l = actStims.get(key);
			int st=0;
			if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,key)) {
				st = Stimulus.getRefStimulus(l, sky);
			}
			else {
				st = Stimulus.getOwnStimulus(l);
			}
			ret.put(key, st);
		}
		return ret;
	}
	
	
	@Override
	public java.util.Map<String, ArrayList<Stimulus>> collectAllStimulusInside(Set<String> types, Set<String> astronomyStatus) {
		java.util.Map<String, ArrayList<Stimulus>> ret = super.collectAllStimulusInside(types, astronomyStatus);
		Entity[] list = getContains();
		for(Entity l:list) {
			HolderUtils.MergeStimulus(ret, l.collectAllStimulusInside(types, astronomyStatus));
		}
		return ret;
	}
	@Override
	public ArrayList<Stimulus> collectAllStimulusInside(String type, Set<String> astronomyStatus) {
		ArrayList<Stimulus> ret = super.collectAllStimulusInside(type, astronomyStatus);
		Entity[] list = getContains();
		for(Entity l:list) {
			ret.addAll(l.collectAllStimulusInside(type, astronomyStatus));
		}
		return ret;
	}
	
	public Entity isPresent(String name) {
		return isPresent(name,null);
	}
	public Entity isPresent(String name,Entity start) {
		LOG.trace("Searching for: "+name);
		LOG.trace("start: "+(start!=null?start.getFullObjectName():"null"));
		if(StringUtils.isBlank(name)) return null;
		String n = StringUtil.exEkezet(name);
		if(start!=null) {
			if(!contains.contains(start)) {
				start = null;
			}
		}
		Entity[] eList = getContains();
		boolean found = false;
		for(Entity obj:eList) {
			LOG.trace("Check: " + obj.getFullObjectName());
			if(obj==start) {
				found = true;
			}
			if(((start==null)||found) && (obj.callName(n)))
				return obj;
		}
		return null;
	}
	@Override
	public boolean addObjInside(Entity obj) {
		synchronized (this) {
			if(status==STATUS_DESTROYED) return false;
			contains.add(obj);
			obj.setEnvironment(this);
			return true;
		}
	}
	@Override
	public boolean moveObjInside(Entity obj, Event evt) {
		ThreadContext.push("room: " + getFullObjectName());

		if(!addObjInside(obj)) return false;
		
		if((obj instanceof Living) && (hasJs()) && (StringUtils.isNotBlank(jsEntryEvent))) {
			try {
				Invocable invocable = evalJs();
				java.lang.Object funcResult = invocable.invokeFunction(jsEntryEvent,this,(Living)obj);
		        if(funcResult instanceof Event) {
		        	evt = (Event)funcResult;
		        }
			} catch (Exception e) {
				LOG.catching(Level.FATAL, e);
			}
		}
		if(evt==null) {
			String txtEnv=null;
			String txtEnvSource;
			String txtSource = null;
			
			if(obj instanceof Living) {
				
				if(StringUtils.isNotBlank(getTxtEntryEnvSource())) {
					txtEnvSource = getTxtEntryEnvSource();
				}
				else {
					txtEnvSource="%S érkezett.";
				}
				if(StringUtils.isNotBlank(getTxtEntrySource())) {
					txtSource = getTxtEntrySource();
				}
			}
			else {
				txtEnvSource="%S jelent meg.";
			}
			evt = Event.createSimpleSourceEvent(Defaults.Sense_Latas, getStimulus(Defaults.Sense_Latas), obj, txtEnvSource, txtEnv, txtSource);
		}
		if(evt!=null) {
			evt.fire();
		}
		if(obj instanceof Living) {
			String description = getDescription((Living)obj);
			if(StringUtils.isNotBlank(description)) {
				((Living)obj).tell(description);
			}
			// meghivjuk az osszes living triggeret.
			List<Living> livings = getAllLiving();
			for(Living l:livings) {
				if(l!=obj) {
					l.triggerEntry((Living)obj);
				}
			}
		}
		ThreadContext.pop();
		return true;
	}
	/**
	 * Szoba leirasat generalja ki, hosszu leiras, hallassal, szaglassal, szornyekkel, targyakkal
	 * 
	 * @param obj elo akinek leirjuk
	 * @return szoba leirasa a elo szemszogebol
	 */
	private String getDescription(Living obj) {
		return getDescription(obj,!obj.getIsShortDescr());
	}
	@Override
	public String getDescription(Living obj, boolean longDescr) {
		RoomDescription rd = getRoomDescription(obj, longDescr);
		
		StringBuffer buff = new StringBuffer();
		buff.append("\n");
		String latas = rd.getSenseDescription(Defaults.Sense_Latas);
		LOG.trace("latas: " + latas);
		if(StringUtils.isNotBlank(latas)) {
			buff.append(Defaults.Color_latas);
			buff.append(latas);
			buff.append(Colorize.RESET);
			buff.append("\n");
		}
		String hallas = rd.getSenseDescription(Defaults.Sense_Hallas);
		LOG.trace("hallas: " + hallas);
		if(StringUtils.isNotBlank(hallas)) {
			buff.append(Defaults.Color_hallas);
			buff.append(hallas);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		String szaglas = rd.getSenseDescription(Defaults.Sense_Szaglas);
		LOG.trace("szaglas: " + szaglas);
		if(StringUtils.isNotBlank(szaglas)) {
			buff.append(Defaults.Color_szaglas);
			buff.append(szaglas);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		String magia = rd.getSenseDescription(Defaults.Sense_Magia);
		LOG.trace("magia: " + magia);
		if(StringUtils.isNotBlank(magia)) {
			buff.append(Defaults.Color_magia);
			buff.append(magia);
			buff.append(Colorize.RESET);
			buff.append("\n");			
		}
		
		buff.append(Defaults.Color_exits);
		buff.append("[");
		buff.append(StringUtil.listToString(rd.getExits()));
		buff.append("]");
		buff.append(Colorize.RESET);
		buff.append("\n\n");

		List<String> middle = HolderUtils.orderResultList(rd.getLivings());
		String livings = StringUtil.listToString(middle);
		if(StringUtils.isNotBlank(livings)) {
			buff.append(Defaults.Color_livings);
			buff.append(livings);
			buff.append(Colorize.RESET);
			buff.append("\n\n");
		}
		middle = HolderUtils.orderResultList(rd.getObjects());
		String items = StringUtil.listToString(middle);
		if(StringUtils.isNotBlank(items)) {
			buff.append(Defaults.Color_objects);
			buff.append(items);
			buff.append(Colorize.RESET);
			buff.append("\n\n");
		}
		return buff.toString();
	}
	
	public RoomDescription getRoomDescription(Living obj, boolean longDescr) {
		ThreadContext.push("room: " + getFullObjectName());
		// jatekos mashonnet is megkapja az informacio
		//if(obj instanceof Player) return null;
		// esemeny osszeszedese: Este/hajnal, stb
		// fenyek az egrol..
		Set<String> astronomyStatus = astronomy.getAllStates();
		if(LOG.isTraceEnabled()) {
			LOG.trace("Ast status: " + String.join(";", astronomyStatus));
		}
		
		java.util.Map<String, ArrayList<Sense>> senseMap = obj.getSenseMapByType(Defaults.senses);
		
		java.util.Map<String,ArrayList<Stimulus>> map = collectAllStimulus(Defaults.senses,astronomyStatus);
	
		java.util.Map<String, Integer> ints = getStimulus(Defaults.senses,astronomyStatus);
		
		//obj.tell("feny ->" + ints.get(Defaults.Sense_Latas));
		
		HashMap<String,String> senseDescription = new HashMap<>();
		for(String key:Defaults.senses) {
			String descr = getDescription(senseMap.get(key),key,map.get(key),longDescr,ints.getOrDefault(key, 0));
			senseDescription.put(key, descr);
		}
		ArrayList<String> exits = getExits(senseMap,ints);
		
		ArrayList<Entity> objList = new ArrayList<>();
		ArrayList<Entity> livList = new ArrayList<>();
		Entity[] eList = getContains();
		for(Entity itm:eList) {
			if(itm instanceof Living) {
				LOG.trace("Found living: " + itm.getFullObjectName());
				if(itm!=obj) livList.add(itm);
			}
			else {
				objList.add(itm);
			}
		}
		ArrayList<Sense> senseList = obj.getSenseByType(Defaults.senses);
		List<String> livings = HolderUtils.getObjects(senseList, ints, livList,"valaki");
		List<String> items = HolderUtils.getObjects(senseList, ints, objList,"valami");
	
		ThreadContext.pop();

		return new RoomDescription(senseDescription, exits, items, livings);

	}
	
	private String getDescription(ArrayList<Sense> sense,String type, ArrayList<Stimulus> descrList, boolean longDescr, int env) {
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
				buff.append(shortDescr?s.getShortDescr(this):s.getDescr(this));
				hasNormal = true;
			}
			if((res > Perception.ALL) &&( sense.get(0).getPerceptionType()==Perception.TYPE_SOURCE)) {
				buff.append(shortDescr?s.getShortDescr(this):s.getDescr(this));
				buff.append("\n");
				hasNormal = true;
			}
		}
		if(!hasNormal) {
			String defstr = Defaults.getDefaultDescriptionForSense(type, max);
			if(StringUtils.isNotBlank(defstr)) {
				buff.append(defstr);
				buff.append("\n");
			}
		}
		return buff.toString();
	}
	/*
	public int canNotice(Living who,Entity obj, String sense) {
		ArrayList<Sense> senseList = who.getSenseByType(sense);
		int environment = getStimulus(sense);
		int diff = obj.getPerceptibility(sense);
		return Perception.test(senseList, environment, diff);
	}
	*/
	private ArrayList<String> getExits(java.util.Map<String,ArrayList<Sense>> sense, java.util.Map<String, Integer> environment){
		ArrayList<String> ret = new ArrayList<>();
		for(Exit e:exits) {
			List<SenseTest> tList=e.getNotice();
			for(SenseTest t:tList) {
				ArrayList<Sense> senseList = sense.get(t.getName());
				if((senseList!=null)&&(!senseList.isEmpty())) {
					int res = Perception.test(senseList, environment.getOrDefault(t.getName(),0), t.getDifficulty());
					if(LOG.isTraceEnabled()) {
						LOG.trace("Exit: "+ e.getDirection() + " notice: " + t.getDifficulty() + "environment: " + environment.getOrDefault(t.getName(),0) + " success:" + res);
					}
					if(res== Perception.ALL) {
						ret.add(e.getDirection());
						break;
					}
				}
			}
		}
		return ret;
	}
	
	@Override
	public boolean removeObjInside(Entity ent) {
		synchronized (this) {
			contains.remove(ent);
			ent.setEnvironment(null);
			return true;
		}
	}
	@Override
	public boolean moveObjectFromInside(Entity ent, Event evt) {
		if(evt!=null) {
			evt.fire();
		}
		// event kezeles ha nincs
		if(!removeObjInside(ent)) return false;
		return true;
	}
	
	
	public ArrayList<Exit> getExits() {
		return exits;
	}
	public void setExits(ArrayList<Exit> exits) {
		this.exits = exits;
	}
	public ArrayList<ObjectRef> getObjRefs() {
		return objRefs;
	}
	public void setObjRefs(ArrayList<ObjectRef> objRefs) {
		this.objRefs = objRefs;
	}
	public String getTxtEntryEnvSource() {
		return txtEntryEnvSource;
	}
	public void setTxtEntryEnvSource(String txtEntryEnvSource) {
		this.txtEntryEnvSource = txtEntryEnvSource;
	}
	public String getTxtEntrySource() {
		return txtEntrySource;
	}
	public void setTxtEntrySource(String txtEntrySource) {
		this.txtEntrySource = txtEntrySource;
	}
	
	public void reset() {
		synchronized (this) {
			HolderUtils.handleObjRef(objRefs, engine, this,false);
			// ugyanezt a randomhoz is
		}
		if((hasJs())&&(hasJsFunction("reset"))) {
			try {
				Invocable invocable = evalJs();
				invocable.invokeFunction("reset",this);
			} catch (Exception e) {
				LOG.catching(Level.FATAL, e);
			}
		}
		
	}
	public Entity[] getContains() {
		synchronized (contains) {
			Entity[] ret = new Entity[contains.size()];
			ret =  contains.toArray(ret);
			return ret;
		}
	}
	@Override
	protected void _destr() {
		for(Entity obj:contains) {
			obj._destrObject();
		}
		if(isOpen()) {
			Handler astronomy =engine.getHandler(Astronomy.REG_NAME);
			if((astronomy!=null)&&(astronomy instanceof Astronomy)){
				((Astronomy)astronomy).unRegisterRoom(this);	
				
			}
			
		}
		CoreTools.unRegisterHeartBeat(engine, this);
	}
	
	@Override
	protected boolean canDestroy() {
		for(Entity obj:contains) {
			if(!obj.canDestroy()) return false;
		}
		return true;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		boolean old = open;
		this.open = open;
		if(status!=Object.STATUS_OK) return;
		if((open!=old) &&(astronomy!=null)) {
			if(open) {
				astronomy.registerRoom(this);
			}
			else {
				astronomy.unRegisterRoom(this);	
			}
		}
	}
	public void tellRoom(String what) {
		Entity[] entList = getContains();
		for(Entity obj:entList) {
			if(obj instanceof Living) {
				((Living)obj).tell(what);
			}
		}
	}

	public ArrayList<RoomItem> getRoomItems() {
		return items;
	}
	/*
	public void setItems(ArrayList<RoomItem> items) {
		this.items = items;
	}
	*/
	public void addAreaEffect(AreaEffect af) {
		synchronized (areaEffects) {
			areaEffects.add(af);
		}
	}
	public void removeAreaEffect(AreaEffect af) {
		synchronized (areaEffects) {
			areaEffects.remove(af);
		}
	}
	
	public Map getMap() {
		return map;
	}
	protected Map generateMap() {
		if(StringUtils.isBlank(mapFile)) return null;
		// terkep betoltese a filebol
		// lecachelni az engine-ben
		return engine.getMap(mapFile);
	}
	public String getMapName() {
		String n=getName();
		return n.substring(n.lastIndexOf("/")+1,n.length());
	}
	@Override
	public Set<String> getEnvironmentStatus() {
		return astronomy.getAllStates();
	}
	@Override
	public Holder getTopHolder() {
		return this;
	}
	
	@Override
	public List<Entity> getItems(Living source) {
		Entity[] array = getContains();
		ArrayList<Entity> list = new ArrayList<>(); 
		for(Entity e:array) {
			if(e instanceof Item) {
				list.add(e);
			}
		}
		return HolderUtils.getObjects(this, source, list);
	}
	public List<Living> getAllLiving(){
		Entity[] array = getContains();
		ArrayList<Living> list = new ArrayList<>();
		for(Entity e:array) {
			if(e instanceof Living) {
				list.add((Living)e);
			}
		}
		return list;
	}
	public List<Entity> getLivings(Living source){
		Entity[] array = getContains();
		ArrayList<Entity> list = new ArrayList<>();
		for(Entity e:array) {
			if(e instanceof Living) {
				list.add(e);
			}
		}
		return HolderUtils.getObjects(this, source, list);

	}
	
	public boolean isCantRest() {
		return cantRest;
	}
	public void setCantRest(boolean cantRest) {
		this.cantRest = cantRest;
	}
	@Override
	public Holder getTopLiving() {
		return null;
	}

	
	
	
	
}
