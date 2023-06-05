package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.ObjectRef;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.Mud.utils.HolderUtils;
import org.ignis.javaMud.utils.Colorize;
/**
 * Altalanos tarolo doboz.
 * @author Ignis
 *
 */
@XmlRootElement(name ="Container")
public class Container extends Item implements Holder {

	static private Logger LOG = LogManager.getLogger(Container.class);
	// suly? meretkorlat?

	/**
	 * dobozhoz tartozo objektumok, de csak mint path, meg fel kell olvasni oket
	 */
	@XmlElement(name="objRef")
	private ArrayList<ObjectRef> objRefs;
	
	/**
	 * doboz tartalma (nem mentheto)
	 */
	@XmlTransient
	private List<Item> contains;
	
	public Container() {
		super();
		contains = Collections.synchronizedList(new ArrayList<Item>());
		objRefs = new ArrayList<>();

	}
	
	@Override
	public boolean addObjInside(Entity obj) {
		if(!(obj instanceof Item)) return false;
		synchronized (this) {
			if(status==STATUS_DESTROYED) return false;
			contains.add((Item)obj);
			obj.setEnvironment(this);
			return true;
		}
	}

	@Override
	public Set<String> getEnvironmentStatus() {
		if(getEnvironment()!=null) {
			return getEnvironment().getEnvironmentStatus();
		}
		return new HashSet<String>();
	}

	@Override
	protected void init() {
		super.init();
	}


	@Override
	protected void _destr() {
		Holder env= getEnvironment();
		if(env==null) {
			for(Entity obj:contains) {
				obj._destrObject();
			}
		}
		else {
			for(Entity obj:contains) {
				obj.moveObject(env, null, null);
			}
		}
		super._destr();
	}

	@Override
	protected boolean canDestroy() {
		Holder env= getEnvironment();
		if(env==null) {
			for(Item obj:contains) {
				if(!obj.canDestroy()) return false;
			}
		}
		return true;
	}

	@Override
	public Holder getTopHolder() {
		Holder env = getEnvironment();
		if(env!=null) {
			return env.getTopHolder();
		}
		return this;
	}
	@Override
	public Holder getTopLiving() {
		Holder env = getEnvironment();
		if(env!=null) {
			return env.getTopLiving();
		}
		return null;
	}

	@Override
	public boolean moveObjInside(Entity ent, Event evt) {
		return addObjInside(ent);
	}

	@Override
	public boolean removeObjInside(Entity ent) {
		if(!(ent instanceof Item)) return false;
		synchronized (this) {
			contains.remove(ent);
			ent.setEnvironment(null);
			return true;
		}
	}

	@Override
	public boolean moveObjectFromInside(Entity ent, Event evt) {
		return removeObjInside(ent);
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
		for(Entity obj:contains) {
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
	@Override
	public int getStimulus(String type) {
		Set<String> astronomyStatus = getEnvironmentStatus();
		return getStimulus(type, astronomyStatus);
	}
	@Override
	public int getStimulus(String type,Set<String> astronomyStatus) {
		
		if(StringUtils.isBlank(type)) return 0;
		type = StringUtil.exEkezet(type);
		ArrayList<Stimulus> actStiom = collectAllStimulus(type, astronomyStatus);
		if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,type)) {
			return Stimulus.getRefStimulus(actStiom, 0);
		}
		else {
			return Stimulus.getOwnStimulus(actStiom);
		}
	
	}

	@Override
	public String getDescription(Living obj, boolean longDescr) {
		StringBuffer buff = new StringBuffer();
		buff.append( super.getDescription(obj, longDescr));
		
		String items = getObjectDescription(obj);
		if(StringUtils.isNotBlank(items)) {
			buff.append("A következő dolgokat találod benne:\n");
			buff.append(Defaults.Color_objects);
			buff.append(items);
			buff.append(Colorize.RESET);
			buff.append("\n");
		}
		else {
			buff.append("Üres\n");
		}
		
		return buff.toString();
	}
	
	public String getObjectDescription(Living obj) {
		ArrayList<Entity> list = new ArrayList<>();
		Entity[] entList;
		synchronized (contains) {
			entList = new Entity[contains.size()];
			entList = contains.toArray(entList);
		}
		for(Entity itm:contains) {
			if(!(itm instanceof Living) ){
				if(itm!=obj) list.add(itm);
			}
		}
		return HolderUtils.getObjectDescription(this,obj, list, "valami");
	}

	@Override
	public boolean look(String what, Living source) {
		return false;
	}

	void afterUnmarshal(Unmarshaller unmarshaller, java.lang.Object parent) {
		LOG.trace("After afterUnmarshal");
		
		HolderUtils.handleObjRef(objRefs, engine, this,false);

	}
	public Entity[] getContains() {
		synchronized (contains) {
			Entity[] ret = new Entity[contains.size()];
			ret =  contains.toArray(ret);
			return ret;
		}
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
	@Override
	public Map<String, ArrayList<Stimulus>> collectAllStimulusInside(Set<String> types, Set<String> astronomyStatus) {
		Map<String, ArrayList<Stimulus>> ret = super.collectAllStimulusInside(types, astronomyStatus);
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

}
