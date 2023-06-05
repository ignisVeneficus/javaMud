package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.InflectionWord;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
/**
 * Targy es elo kozos ososztaly
 * A szobak es dobozok be-kirakas vegzi
 * @author Ignis
 *
 */
public abstract class Entity extends Object {
	static private Logger LOG = LogManager.getLogger(Entity.class);

	private InflectionWord shortName;
	
	@XmlElement(name = "id")
	private ArrayList<String> ids;

	/**
	 * Amiben talalhato
	 */
	@XmlTransient
	private Holder environment;
	public void moveObject(String where, Event from, Event to) {
		Singleton newPlace = engine.getOrLoad(where);
		
		// hibakezeles az uj osztaly felolvasa eseten
		if(newPlace == null) {
			LOG.error(where + " nem letezik!");
		}
		if(!(newPlace instanceof Holder)) {
			LOG.error(where+ " nem Holder!");
		}
		moveObject((Holder)newPlace, from, to);
	}
	
	public void moveObject(Holder where, Event from, Event to) {
		if(where == null) {
			LOG.error("Try to move " +this.getFullObjectName()+ " to VOID");
			return;
		}
		boolean ok = true;
		Holder oldEnv = getEnvironment();
		if(oldEnv!=null) {
			ok = oldEnv.moveObjectFromInside(this, from);
		}
		if(ok) {
			ok = where.moveObjInside(this,to);
			if(!ok) {
				LOG.error("Cant move: " + this.getFullObjectName() + " to: " +where.getFullObjectName());
				if(oldEnv!=null) {
					ok = oldEnv.addObjInside(this);
					if(!ok) {
						LOG.fatal("Cant move back: " + this.getFullObjectName() + " to: " +oldEnv.getFullObjectName());
						
					}
				}
			}
		}
		else {
			LOG.error("Cant remove: " + this.getFullObjectName() + " from: " +oldEnv.getFullObjectName());
			
		}
	}
	public void moveObject(Holder where) {
		if(where == null) {
			LOG.error("Try to move " +this.getFullObjectName()+ " to VOID");
			return;
		}
		boolean ok = true;
		Holder oldEnv = getEnvironment();
		if(oldEnv!=null) {
			ok = oldEnv.removeObjInside(this);
		}
		if(ok) {
			ok = where.addObjInside(this);
			if(!ok) {
				LOG.error("Cant move: " + this.getFullObjectName() + " to: " +where.getFullObjectName());
				if(oldEnv!=null) {
					ok = oldEnv.addObjInside(this);
					if(!ok) {
						LOG.fatal("Cant move back: " + this.getFullObjectName() + " to: " +oldEnv.getFullObjectName());
						
					}
				}
			}
		}
		else {
			LOG.error("Cant remove: " + this.getFullObjectName() + " from: " +oldEnv.getFullObjectName());
			
		}
			
	}
	public Holder getTopEnvironment() {
		Holder env = getEnvironment();
		if(env!=null) {
			return env.getTopHolder();
		}
		return null;
	}
	public Holder getEnvironment() {
		synchronized (this) {
			return environment;
		}
	}

	public void setEnvironment(Holder environment) {
		synchronized (this) {
			this.environment = environment;
		}
	}
	@Override
	protected void _destr() {
		Holder env = getEnvironment();
		env.removeObjInside(this);
		setEnvironment(null);
		super._destr();
	}

	
	abstract public int getPerceptibility(String type);
	abstract public ArrayList<SenseTest> getPerceptibility(Set<String> type);
	abstract public ArrayList<SenseTest> getPerceptibility(String[] type);
	public ArrayList<SenseTest> getAllPerceptibility(){
		return getPerceptibility(Defaults.senses);
	}
	
	abstract public boolean action(String command, String[] param, Living source);

	@Override
	protected void init() {
		super.init();
	}
	public InflectionWord getShortName() {
		if(shortName==null) {
			LOG.fatal("No shortName: " + getFullObjectName());
			return new InflectionWord();
		}
		return shortName;
	}
	public String getShortNameString() {
		return getShortName().getWord();
	}
	public void setShortNameString(String str) {
		if(shortName==null) {
			shortName= new InflectionWord();
		}
		shortName.setWord(str);
	}
	
	public void setShortNameTargy(String str) {
		if(shortName==null) {
			shortName= new InflectionWord();
		}
		shortName.setTargy(str);
	}
	public String getShortNameTargy() {
		return getShortName().getTargy();
	}

	public void setShortName(InflectionWord shortName) {
		this.shortName = shortName;
	}
	@Override
	public boolean callName(String name) {
		if (super.callName(name))
			return true;
		
		if (ids == null) {
			LOG.fatal("nincs ID lista");
			return false;
		}
		for (String id : ids) {
			LOG.trace("check: " + name + " vs " + id);
			if (StringUtil.equalsSecoundString(name, id)) {
				return true;
			}
		}
		return false;
	}
	public ArrayList<String> getIds() {
		return ids;
	}

	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
	
	public Event getAppearEvent() {
		return null;
	}

	public Event getDisappearEvent() {
		return null;
	}

}
