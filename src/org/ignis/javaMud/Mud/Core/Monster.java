package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.Emotion;
import org.ignis.javaMud.Mud.dataholder.EmotionList;
import org.ignis.javaMud.Mud.dataholder.monster.Appear;
import org.ignis.javaMud.Mud.dataholder.monster.Disappear;
import org.ignis.javaMud.Mud.professions.Profession;
import org.ignis.javaMud.Mud.professions.ProfessionFactory;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.xml.JsMapAdapter;

@XmlRootElement(name ="Living")
@XmlAccessorType(XmlAccessType.FIELD)
public class Monster extends Living {
	static private Logger LOG = LogManager.getLogger(Monster.class);
	/**
	 * Szakmak, amiket kepvisel: fuggveny halmazok, pl:
	 * skillmonster -> skill tanitas
	 * barkeeper -> kocsmaros
	 * stb.
	 * 
	 */
	@XmlElement(name="profession")
	private ArrayList<org.ignis.javaMud.Mud.dataholder.monster.Profession> professionsBase;
	/**
	 * Maximum hany szorny lehet osszesen.
	 */
	@XmlAttribute(name="max")
	private int maxQty;
	
	/**
	 *  van-e anyagi formaja, aka utheto-e normal fegyverrel, hat-e ra a fizika stb..
	 */
	@XmlAttribute(name="hasMaterialForm")
	private boolean materialForm;
	/**
	 * Megjelenos szoveg
	 */
	@XmlElement(name="appear")
	private Appear appear;
	/**
	 * Eltunos szoveg
	 */
	@XmlElement(name="disappear")
	private Disappear disappear;
	
	/**
	 * azok erzesek listaja, amit a szorny "csinal"
	 */
	@XmlElement(name="emotions")
	private EmotionList emotions;

	/**
	 * Betolto
	 */
	@XmlTransient
	private MonsterLoader loader;
	
	/**
	 * Hallgato javascripket. a "Mondja: "-ra valaszol
	 * Action map, a actiont (action elso szava) mappeli ossze a js fuggvenyekkel
	 */
	@XmlElement(name="listen")
	@XmlJavaTypeAdapter(JsMapAdapter.class)
	private HashMap<String,String> jsListenAction;
	
	@XmlTransient
	private List<Profession> professions;
	
	public int getMaxQty() {
		return maxQty;
	}

	public void setLoader(MonsterLoader loader) {
		this.loader = loader;
	}

	@Override
	protected void _die() {
		destr();
	}

	@Override
	protected void _destr() {
		super._destr();
		if(loader!=null) {
			loader.substractOld();
		}
	}

	@Override
	protected void init() {
		professions = new ArrayList<>();
		if(disappear!=null) {
			// ????
		}
		if(professionsBase!=null) {
			for(org.ignis.javaMud.Mud.dataholder.monster.Profession p:professionsBase) {
				LOG.info("Prof-> " +p.getName());
				Profession prof=ProfessionFactory.create(p.getName());
				if(prof!=null) {
					LOG.info("found");
					professions.add(prof);
				}
			}
		}
		
		super.init();

		
	
		
		
		Sense s = new Sense("látás",Perception.TYPE_REFLECTION,50,30,Defaults.Sense_Latas);
		addSense(s);
		
		s = new Sense("hallás",Perception.TYPE_SOURCE,50,15,Defaults.Sense_Hallas);
		addSense(s);
		
		s = new Sense("szaglás",Perception.TYPE_SOURCE,50,15,Defaults.Sense_Szaglas);
		addSense(s);
		
		s = new Sense("mágia",Perception.TYPE_INDIVIDUAL,90,7,Defaults.Sense_Magia);
		addSense(s);
		
		
		
	}
	
	@Override
	public void tick() {
		LOG.trace("Tick");
		super.tick();
		if(emotions!=null) {
			if((emotions.tick() && (emotions.getEmotionList().size()>0) && emotions.getProbality()>0)) {
				LOG.trace("Has emotion: P:" + emotions.getProbality());
				int rnd = ThreadLocalRandom.current().nextInt(0, emotions.getProbality());
				LOG.trace("rnd:" + rnd);
				if(rnd!=0) return;
				
				ArrayList<Emotion> eList = new ArrayList<>();
				List<Emotion> oldList = emotions.getEmotionList();
				Set<String> s = getEnvironmentStatus();
				for(Emotion e:oldList) {
					if(StringUtils.isBlank(e.getCondition())) {
						eList.add(e);
					}
					else {
						String cond = StringUtil.exEkezet(e.getCondition());
						if(s.contains(cond)) {
							LOG.trace("Found :" + cond);
							eList.add(e);
						}
					}
				}
				LOG.trace("Emotion list: " + eList.size());
				if(eList.size()>0) {
					rnd = ThreadLocalRandom.current().nextInt(0, eList.size());
					Emotion e = eList.get(rnd);
					
					Event event = Event.createSimpleSourceEvent(e.getTests(), this, e.getDescr(), "", "");
					event.fire();
				}
			}
		}
		
	}

	@Override
	public Event getAppearEvent() {
		if(appear!=null) {
			return appear.getEvent();
		}
		return super.getAppearEvent();
	}

	@Override
	public Event getDisappearEvent() {
		if(disappear!=null) {
			return disappear.getEvent();
		}
		return super.getDisappearEvent();
	}

	@Override
	public boolean hasMaterialForm() {
		return materialForm;
	}

	@Override
	public boolean action(String command, String[] param, Living source) {
		if(professions!=null) {
			for(Profession p:professions) {
				if(p.action(command, param, source)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void tell(String what) {
		if(professions!=null) {
			List<String> parsed = Profession.parse(what);
			if(parsed.size()>0) {
				for(Profession p:professions) {
					p.handle(parsed,this);
				}
			}
		}
	}
	public void force(String what) {
		processText(what);
	}

	// agressziv szornyeknel
	@Override
	public void triggerEntry(Living obj) {
		// TODO Auto-generated method stub
		
	}
	
}
