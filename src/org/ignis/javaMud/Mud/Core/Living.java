package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.action.Action;
import org.ignis.javaMud.Mud.dataholder.ObjectRef;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.db.Race;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.handlers.ActionHandler;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.Mud.utils.HolderUtils;
import org.ignis.javaMud.utils.Colorize;
import org.ignis.javaMud.utils.CoreTools;
import org.ignis.javaMud.utils.xml.DiceIntegerAdapter;
/**
 * Minden ami el, mozog:
 * jatekos, szornyek NJK-k
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Living extends Entity implements Holder, HeartBeatListener {
	static private Logger LOG = LogManager.getLogger(Living.class);

	/**
	 * az elohoz tartozo objektumok
	 */
	@XmlElement(name = "objRef")
	private ArrayList<ObjectRef> objRefs;

	/**
	 * maximalis kitartas pont
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer maxSP;

	/**
	 * aktualis kitartas pont
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actSP;
	/**
	 * Maximalis eletero
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer maxHP;
	/**
	 * Aktualis eletero
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actHP;
	/**
	 * maximalis manapont
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer maxMP;
	/**
	 * aktualus mannapont
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actMP;
	/**
	 * aktualis HP regeneralas
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actHPReg;
	/**
	 * Alap HP regeneralas
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer baseHPReg;
	/**
	 * Aktualis MP regeneralas
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actMPReg;
	/**
	 * Alap MP regeneralas
	 */
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer baseMPReg;

	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer actSPReg;
	@XmlAttribute
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer baseSPReg;

	/*
	 * atdolgozni eleme: race, @name = neve @basedOn -> valos race tipus es itt
	 * johetnek a belso modositok, amik esetleg kellenek.
	 */
	@XmlAttribute(name = "race")
	private String raceStr;

	@XmlAttribute(name = "gender")
	private String gender;
	
	@XmlAttribute(name = "class")
	private String rpgClass;
	/**
	 * Bonus lista, XML-bol csak ezzel lehet a stat-okhoz, sense-hez hozzaferni.
	 */
	@XmlElement(name = "bonus")
	private List<Bonus> xmlBonus;

	/*
	 * ********************************************* localis valtozok
	 * 
	 *********************************************/

	@XmlTransient
	private List<Item> contains;

	@XmlTransient
	private Map<String,Sense> senses;

	@XmlTransient
	private Map<String,Skill> skills;
	
	@XmlTransient
	private Map<String,Stat> stats;
	
	@XmlTransient
	private Race race;

	public Living() {
		contains = Collections.synchronizedList(new ArrayList<Item>());
		senses = Collections.synchronizedMap(new HashMap<String,Sense>());
		skills = Collections.synchronizedMap(new HashMap<String,Skill>());
		
	}

	@Override
	protected void init() {
		super.init();
		CoreTools.registerHeartBeat(engine, this);
		if (actSP == null) {
			actSP = 0;
		}
		if (actHP == null) {
			actHP = 0;
		}
		if (actMP == null) {
			actMP = 0;
		}
		
		if (maxSP == null) {
			maxSP = 0;
		}
		if (maxHP == null) {
			maxHP = 0;
		}
		if (maxMP == null) {
			maxMP = 0;
		}
		if (actHPReg == null) {
			actHPReg = 0;
		}
		if (baseHPReg == null) {
			baseHPReg = 0;
		}
		if (actMPReg == null) {
			actMPReg = 0;
		}
		if (baseMPReg == null) {
			baseMPReg = 0;
		}
		if (actSPReg == null) {
			actSPReg = 0;
		}
		if (baseSPReg == null) {
			baseSPReg = 0;
		}

		if (actHP == null || actHP == 0 || actHP > maxHP) {
			actHP = new Integer(maxHP);
		}
		if (actMP == null || actMP == 0 || actMP > maxMP) {
			actMP = new Integer(maxMP);
		}
		if (actSP == null || actSP == 0 || actSP > maxSP) {
			actSP = new Integer(maxSP);
		}
		
		if(objRefs!=null) {
			HolderUtils.handleObjRef(objRefs, engine, this,false);
		}
		if(xmlBonus!=null) {
			for(Bonus b:xmlBonus) {
				b.setSource(this);
				addBonus(b);
			}
		}

	}

	@Override
	public boolean action(String command, String[] param, Living source) {
		return false;
	}

	public ArrayList<Sense> getSenseByType(String type) {
		ArrayList<Sense> ret = new ArrayList<>();
		String t = StringUtil.exEkezet(type);
		for (Sense s : senses.values()) {
			if (StringUtil.equalsSecoundString(t, s.getSenseType())) {
				ret.add(s.clone());
			}
		}
		return ret;
	}

	public ArrayList<Sense> getSenseByType(Set<String> type) {
		ArrayList<Sense> ret = new ArrayList<>();
		for (Sense s : senses.values()) {
			if (type.contains(s.getSenseType())) {
				ret.add(s.clone());
			}
		}
		return ret;
	}
	public Map<String,ArrayList<Sense>> getSenseMapByType(HashSet<String> senseTypes) {
		Map<String,ArrayList<Sense>> ret = new HashMap<>();
		for (String st : senseTypes) {
			ArrayList<Sense> list = new ArrayList<>();
			ret.put(st, list);
		}
		for (Sense s : senses.values()) {
			String key = s.getSenseType();
			ArrayList<Sense> list = ret.get(key);
			if(list!=null) {
				list.add(s.clone());
			}
		}
		return ret;
		
	}

	@Override
	public int getPerceptibility(String type) {
		LOG.warn("Default ertekekket hasznalva!");
		return Defaults.getDefaultIntensity(type);
	}

	public void tell(String what) {

	}

	public boolean canMove() {
		return true;
	}
	public boolean can(String what) {
		LOG.warn("Nem ellenorizve!");
		return true;
	}

	public boolean getIsShortDescr() {
		return false;
	}

	@Override
	protected void _destr() {
		super._destr();
		CoreTools.unRegisterHeartBeat(engine, this);

	}

	@Override
	protected boolean canDestroy() {
		return true;
	}

	protected List<Sense> getSenses() {
		return new ArrayList<Sense>(senses.values());
	}
	
	protected void addSense(Sense s) {
		senses.put(s.getName(), s);
	}

	public ArrayList<ObjectRef> getObjRefs() {
		return objRefs;
	}

	public void setObjRefs(ArrayList<ObjectRef> objRefs) {
		this.objRefs = objRefs;
	}

	public int getActSP() {
		return actSP;
	}

	public void setActSP(int actSP) {
		synchronized (this) {
			this.actSP = actSP;
		}
	}

	public int getMaxSP() {
		return maxSP;
	}

	public void setMaxSP(int maxSP) {
		this.maxSP = maxSP;
	}

	public void addSP(int sp) {
		synchronized (this) {
			this.actSP += sp;
			if(actSP<0) actSP = 0;
			if(actSP>getMaxSP()) actSP = getMaxSP();
		}
	}

	public void addHP(int hp) {
		synchronized (this) {
			this.actHP += hp;
			if(actHP<0) actHP = 0;
			if(actHP>getMaxHP()) actHP = getMaxHP();
		}
	}
	public void addMP(int mp) {
		synchronized (this) {
			this.actMP += mp;
			if(actMP<0) actMP = 0;
			if(actMP>getMaxHP()) actMP = getMaxMP();
		}
	}
	// kepzettsegek + hasznalata valtzotatja
	public double getSPMultipleForMoving() {
		return 1;
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

	public void minusHp(int hPLost, String why) {
		LOG.trace("HPLost: " + hPLost + " why: " + why);
		addHP(-hPLost);
		tellStatus();
		if (actHP <= 0) {
			die(why);
		}
	}

	public void tellStatus() {
	}
	public void updatedRoom() {
	}
	public void updatedLiving() {
	}
	abstract protected void _die();
	
	public void die(String why) {

		List<SenseTest> tests = getAllPerceptibility();
		Event e = Event.createSimpleSourceEvent(tests, this, "%S " + why + " miatt meghallt", "", StringUtils.capitalize(why + " miatt meghalltál!"));
		if(e!=null) {
			e.fire();
		}

		// megcsinaljuk a hullat ha volt teste
		if (hasMaterialForm()) {
			RottableContainer corpse = (RottableContainer) engine.load("@items/corpse");
			corpse.setProperty("owner", getShortNameString());
			corpse.initBeforeTick();
			Holder h = getEnvironment();
			// nem kellenek esemeny, az megtortenik a die eseten.
			h.addObjInside(corpse);
		}
		_die();
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getActHP() {
		return actHP;
	}

	public void setActHP(int actHP) {
		this.actHP = actHP;
	}

	public int getMaxMP() {
		return maxMP;
	}

	public void setMaxMP(int maxMP) {
		this.maxMP = maxMP;
	}

	public int getActMP() {
		return actMP;
	}

	public void setActMP(int actMP) {
		this.actMP = actMP;
	}

	public int getActHPReg() {
		return actHPReg;
	}

	public void setActHPReg(int actHPReg) {
		this.actHPReg = actHPReg;
	}

	public int getBaseHPReg() {
		return baseHPReg;
	}

	public void setBaseHPReg(int baseHPReg) {
		this.baseHPReg = baseHPReg;
	}

	public int getActMPReg() {
		return actMPReg;
	}

	public void setActMPReg(int actMPReg) {
		this.actMPReg = actMPReg;
	}

	public int getBaseMPReg() {
		return baseMPReg;
	}

	public void setBaseMPReg(int baseMPReg) {
		this.baseMPReg = baseMPReg;
	}

	public int getActSPReg() {
		return actSPReg;
	}

	public void setActSPReg(int actSPReg) {
		this.actSPReg = actSPReg;
	}

	public int getBaseSPReg() {
		return baseSPReg;
	}

	public void setBaseSPReg(int baseSPReg) {
		this.baseSPReg = baseSPReg;
	}

	public Item[] getContains() {
		synchronized (contains) {
			Item[] ret = new Item[contains.size()];
			ret = contains.toArray(ret);
			return ret;
		}
	}

	public void tick() {
		int mxHp = getMaxHP();
		if(getActHP()<mxHp) {
			addHP(getActHPReg());
		}
		int mxMp = getMaxMP();
		if(getActMP()<mxMp) {
			addMP(getActMPReg());
		}
		int mxSp = getMaxSP();
		if(getActSP()<mxSp) {
			addSP(getActSPReg());
		}
	}

	@Override
	public Set<String> getEnvironmentStatus() {
		Holder h = getEnvironment();
		if (h != null) {
			return h.getEnvironmentStatus();
		}
		return null;
	}

	@Override
	public Holder getTopHolder() {
		Holder env = getEnvironment();
		if (env != null) {
			return env.getTopHolder();
		}
		return this;
	}
	public Holder getTopLiving() {
		return this;
	}

	abstract public boolean hasMaterialForm();

	@Override
	public boolean addObjInside(Entity obj) {
		if (!(obj instanceof Item))
			return false;
		synchronized (this) {
			if (status == STATUS_DESTROYED)
				return false;
			contains.add((Item) obj);
			obj.setEnvironment(this);
			return true;
		}
	}

	@Override
	public boolean moveObjInside(Entity obj, Event evt) {
		if (!(obj instanceof Item))
			return false;

		if (!addObjInside(obj))
			return false;
		if (evt != null) {
			evt.fire();
		}
		return true;
	}

	@Override
	public boolean removeObjInside(Entity ent) {
		if (!(ent instanceof Item))
			return false;
		synchronized (this) {
			contains.remove((Item) ent);
			ent.setEnvironment(null);
			return true;
		}
	}

	@Override
	public boolean moveObjectFromInside(Entity ent, Event evt) {
		if (!removeObjInside(ent))
			return false;
		if (evt != null) {
			evt.fire();
		}
		// event kezeles ha nincs
		return true;
	}
	/**
	 * Bonusz hozzaadasa
	 * @param b a bonusz
	 */
	public void addBonus(Bonus b) {
		String type =b.getType();
		if(Defaults.Bonus_Sense.equalsIgnoreCase(type)) {
			for(Sense sense:senses.values()) {
				if(sense.getName().equalsIgnoreCase(b.getName())) {
					sense.addBonus(b);
					return;
				}
			}
			return;
		}
		if(Defaults.Bonus_Skill.equalsIgnoreCase(type)) {
			for(Skill skill:skills.values()) {
				if(skill.getName().equalsIgnoreCase(b.getName())) {
					skill.addBonus(b);
					return;
				}
			}
			return;
		}
		if(Defaults.Bonus_Stat.equalsIgnoreCase(type)) {
			for(Stat stat:stats.values()) {
				if(stat.getName().equalsIgnoreCase(b.getName())) {
					stat.addBonus(b);
					return;
				}
			}
			return;
		}
	}
	/**
	 * osszes bonusz hozzaadasa
	 * @param bonusList bonuszok listaja
	 */
	public void addBonuses(List<Bonus> bonusList) {
		for(Bonus b:bonusList) {
			addBonus(b);
		}
	}
	/**
	 * egy adott bonusz leszedese
	 * @param b bonusz, amit le kell szedi
	 */
	public void removeBonus(Bonus b) {
		String type =b.getType();
		if(Defaults.Bonus_Sense.equalsIgnoreCase(type)) {
			for(Sense sense:senses.values()) {
				if(sense.getName().equalsIgnoreCase(b.getName())) {
					sense.removeBonus(b);
					return;
				}
			}
			return;
		}
		if(Defaults.Bonus_Skill.equalsIgnoreCase(type)) {
			for(Skill skill:skills.values()) {
				if(skill.getName().equalsIgnoreCase(b.getName())) {
					skill.removeBonus(b);
					return;
				}
			}
			return;
		}
		if(Defaults.Bonus_Stat.equalsIgnoreCase(type)) {
			for(Stat stat:stats.values()) {
				if(stat.getName().equalsIgnoreCase(b.getName())) {
					stat.removeBonus(b);
					return;
				}
			}
			return;
		}
	}

	/**
	 * adott objektumhoz tartozo bonuszok leszedese
	 * @param source amihez tartoznak a bonuszok
	 */
	public void removeBonusBySource(java.lang.Object source) {
		for(Sense sense:senses.values()) {
			sense.removeBonusBySource(source);
		}
		for(Skill skill:skills.values()) {
			skill.removeBonusBySource(source);
		}
		for(Stat stat:stats.values()) {
			stat.removeBonusBySource(source);
		}
	}
	
	
	@Override
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
		boolean found = false;
		Item[] cont = getContains();
		for(Item obj:cont) {
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
	public int getStimulus(String type) {
		Set<String> astronomyStatus = getEnvironmentStatus();
		return getStimulus(type, astronomyStatus);
	}
	@Override
	public int getStimulus(String type,Set<String> astronomyStatus) {
		if(StringUtils.isBlank(type)) return 0;
		type = StringUtil.exEkezet(type);
		// osszeszedni a szobaban levo cuccokat is, fenygomb, buzbomba stb..
		ArrayList<Stimulus> actStiom = collectAllStimulus(type, astronomyStatus);
		if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,type)) {
			return Stimulus.getRefStimulus(actStiom, 0);
		}
		else {
			return Stimulus.getOwnStimulus(actStiom);
		}
	
	}
	
	/**
	 * jelenleg nem lehet az inventorit megnezni
	 */
	public boolean look(String what,Living source) {
		return false;
	}
	/**
	 * parancsok vegrehajtasa
	 * @param message parancs
	 */
	protected void processText(String message) {
		LOG.trace(message);
		Holder env = getEnvironment();
		
		//String[] line = message.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		
		//kinyerjuk a commandot
		List<String> partOne = Action.parseCommand(message, 1);
		if(partOne.size()==0) return;
		
		String command = StringUtil.exEkezet(partOne.get(0));

		String[] param = new String[0];
		String paramLine = "";
		if(partOne.size()==2) {
			List<String> partParams = Action.parseCommand(partOne.get(1),0);
			param = new String[partParams.size()];
			param = partParams.toArray(param);
			paramLine = partOne.get(1);
		}
		
		if(env!=null) {
			if(env instanceof Room) {
				// megfuttajuk a szoban
				if(((Room)env).action(command,param, this)) {
					return;
				}
			}
		}
		
		Handler handler = engine.getHandler(ActionHandler.REG_NAME);
		if((handler!=null) && (handler instanceof ActionHandler)) {
			ActionHandler ac = (ActionHandler)handler;
			Action act = null;
			/* kitalalni a sorrendet
			Action act = ac.getClassAction(getRpgClass(), a);
			if(act!=null) {
				if(act.process(p, this)) {
					return;
				}
			}
			
			act = ac.getRaceAction(getRace(), a);
			if(act!=null) {
				if(act.process(p, this)) {
					return;
				}
			}
			*/
			act = ac.getAction(command);
			if(act!=null) {
				if(act.process(paramLine, this)) {
					return;
				}
			}
		}
		/*
		 * targyak egyesevel
		 */
		Item[] items = getContains();
		for(Item i:items) {
			if(i.hasActionFor(command)) {
				boolean b = i.action(command, param,this);
				if(b) return; 
			}
		}
		
		tell("Tessék?");
		
	}
	/**
	 * visszaadja a class-at az elolenynek
	 * @return class neve
	 */
	// TODO atirni listara
	public String getRpgClass() {
		return rpgClass;
	}

	/**
	 * beallitja a class-at az elolenynek
	 * @param rpgClass a kerdeses class neve
	 */
	public void setRpgClass(String rpgClass) {
		this.rpgClass = rpgClass;
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
	
		Map<String, Integer> ints = getEnvironment().getStimulus(Defaults.senses);
		
		//Latashoz hozzakerul a sebesules
		int latasMax = 0;
		for(Stimulus s: latasList) {
			latasMax=Math.max(latasMax, s.getIntensity());
		}
		double per = ((double)getActHP())/getMaxHP();
		Stimulus s = new Stimulus(Defaults.Sense_Latas, latasMax, "\n"+Defaults.getHealthDescription(per), Defaults.getHealthDescription(per),null,0);
		latasList.add(s);
		
		
		ArrayList<Entity> objList = new ArrayList<>();
		Entity[] eList = getContains();
		for(Entity itm:eList) {
			if(itm instanceof Usable) {
				if(((Usable)itm).isUsed()) {
					objList.add(itm);
					
				}
			}
		}
		ArrayList<Sense> senseList = obj.getSenseByType(Defaults.senses);
		List<String> items = HolderUtils.getObjects(senseList, ints, objList,"valami");
		
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
		List<String> middle = HolderUtils.orderResultList(items);
		String itemsstr = StringUtil.listToString(middle);
		LOG.trace("targyak: " + itemsstr);
		if(StringUtils.isNotBlank(itemsstr)) {
			buff.append(Defaults.Color_objects);
			buff.append("Nála van: ");
			buff.append(items);
			buff.append(Colorize.RESET);
			buff.append("\n\n");
		}
		
		return buff.toString();
		
	}

	/**
	 * a holder fuggvenye, eloleny leltarjat nem nezheti meg mas
	 */
	public List<Entity> getItems(Living source){
		return new ArrayList<>();
	}
	/**
	 * a HP hianyat adja vissza
	 * @return
	 */
	public int getHPLost() {
		return getMaxHP() - getActHP();
	}
	public int getSPLost() {
		return getMaxSP() - getActSP();
	}
	public int getMPLost() {
		return getMaxMP() - getActMP();
	}

	@Override
	public Map<String, ArrayList<Stimulus>> collectAllStimulusInside(Set<String> types, Set<String> astronomyStatus) {
		Map<String, ArrayList<Stimulus>> ret = super.collectAllStimulusInside(types, astronomyStatus);
		Item[] list = getContains();
		for(Item l:list) {
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
	
	@Override
	public HashMap<String,Integer> getStimulus(Set<String> types){
		Set<String> astronomyStatus = getEnvironmentStatus();
		return getStimulus(types, astronomyStatus);
	}

	@Override
	public HashMap<String,Integer> getStimulus(Set<String> types,Set<String> astronomyStatus){
		java.util.Map<String,ArrayList<Stimulus>> actStims = collectAllStimulusInside(types, astronomyStatus);
		HashMap<String, Integer> ret = new HashMap<>();
		for(String key:types) {
			ArrayList<Stimulus> l = actStims.get(key);
			int st=0;
			if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,key)) {
				st = Stimulus.getRefStimulus(l,0);
			}
			else {
				st = Stimulus.getOwnStimulus(l);
			}
			ret.put(key, st);
		}
		return ret;
	}

	/**
	 * mozgasgatlo tenyezo, 0-1 (aranyban)
	 * @return 0 ha nincs megterhelve, 1 ha nem tud mozogni.
	 */
	public double getMGT() {
		return 0;
	}

	/**
	 * Egy adott skill lekerdezes
	 * @param name skill neve
	 * @return skill, null na nincs
	 */
	public Skill getSkill(String name) {
		return skills.get(name);
	}
	/**
	 * Visszadja az osszes skillt
	 * @return az eloleny osszes skillje
	 */
	public List<Skill> getAllSkill(){
		return new ArrayList<Skill>(skills.values());
	}
	
	/**
	 * Sebez az adott elolenyen, lekezeli az ellenallasokat, vedelmeket
	 * @param type sebzes tipusa (ellenallasok miatt)
	 * @param hpLost mennyit sebez
	 * @param reason ok, mi, miert van a sebzes, ha meghall akkor lehessen tudni miert
	 */
	public void sebez(String type, int hpLost, String reason) {
		// ellenallasasok
		if(hpLost<=0) return;
		
		// SFE
		minusHp(hpLost, reason);
		
	}
	/**
	 * Adott skillt-t (kepzettseget) add az elolenyhez
	 * @param name a skill neve
	 */
	public void addSkill(String name) {
		if(skills.containsKey(StringUtil.exEkezet(name))) return;
		Skill s = engine.getSkill(name);
		if(s!=null) {
			skills.put(StringUtil.exEkezet(s.getName()),s);
		}
	}
	
	/**
	 * Eszreveszi-e az adott entitast, vagy sem
	 * @param ent az entitas amit meg akar figyelni
	 * @return Perception ertek
	 */
	public int canNotice(Entity ent) {
		if(getTopEnvironment()!=ent.getTopEnvironment()) {
			return Perception.NONE_BELOW;
		}
		Map<String, Integer> environment = getTopEnvironment().getStimulus(Defaults.senses);
		ArrayList<Sense> senseList = getSenseByType(Defaults.senses);
		
		ArrayList<SenseTest> tests = ent.getPerceptibility(Defaults.senses);
		return Perception.test(senseList, environment, tests,Perception.T_ANY);
	}

	abstract public void triggerEntry(Living obj) ;

	/**
	 * lekerdezni, hogy van-e adott szamu szabad fogo vegtag a fegyverek megfogasahoz
	 * @param nrLimbs hany vegtagnak kell lennie
	 * @return van-e annyi vegtag
	 */
	public boolean hasHoldingLimbs(int nrLimbs) {
		// TODO
		return true;
	}
	/**
	 * lefoglal adott szamu vegtagot
	 * @param nrLimbs hany vegtagot kell lefoglalnia
	 * @return lista, a vegtag neveivel, amennyiben null, nincs eleg vegtag
	 */
	public List<String> lockHoldingLimbs(int nrLimbs){
		// TODO
		ArrayList<String> ret = new ArrayList<>();
		ret.add("jobb kéz");
		return ret;
	}

	/**
	 * felszabaditja a vegtagokat
	 * @param libs a vegtagok listaja
	 */
	public void releasekHoldingLimbs(List<String> libs){
		
	}
	
	
}
