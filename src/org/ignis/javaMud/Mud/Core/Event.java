package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.Test;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
/**
 * eszrevehetoseg
 * feny eseten 100-el elosztjuk es annyival szorozzuk a cuccot.
 * hang es egyeb eseten kivonjuk belole a kornyezetet
 * 
 * 
 * erzekeles (event) workflow:
 * - valaki eloallitja az esementy, cel/forras alapjan, es elinditja
 * - az event meghatarozza a forras es cel environmentjet (szobakat)
 * - ha nem ugyanaz, mindkettobe beloki az esemenyt (duplikacio kiszurese)
 * - a szoba mindenkinek (kiveve cel/forras) kiszamitja, mit ismerhet belole es elsuti a szovegeket 
 */

public class Event {
	
	static private Logger LOG = LogManager.getLogger(Event.class);
	/**
	 * tesztek, amiket le kell futtani
	 */
	private ArrayList<SenseTest> tests;
	/**
	 * tipus: ANY vagy ALL
	 * minden tesztnek ok-nak kell lennie, vagy eleg csak 1
	 */
	private String testType;
	
	/**
	 * Forras, aki kivaltotta
	 */
	private Entity source;
	/**
	 * Celpont, akire hat (ha van)
	 */
	private Entity target;
	/**
	 * Esemenyszoveg ami forras+celpont szobajaban jelenik meg
	 */
	private String txtEnvSourceTarget;
	/**
	 * Esemenyszoveg ami a celpont szobajaban jelenik meg
	 */
	private String txtEnvTarget;
	/**
	 * Esemenyszoveg ami a forras szobajaban jelenik meg
	 */
	private String txtEnvSource;
	/**
	 * Esemenyszoveg a szobaban jelenik meg (nem latszik/nincs ott a forras/celpont)
	 */
	private String txtEnv;
	
	/**
	 * Esemenyszoveg ami celpontnal jelenik meg ha latszik/tudhato a forras
	 */
	private String txtTargetSource;
	/**
	 * Esemenyszoveg ami celpontnal jelenik meg
	 */
	private String txtTarget;
	/**
	 * Esemenyszoveg ami a forrasnal jelenik meg, ha van celpont
	 */
	private String txtSourceTarget;
	/**
	 * Esemenyszoveg ami a forrasnal jelenik meg
	 */
	private String txtSource;

	public Entity getSource() {
		return source;
	}

	public void setSource(Entity source) {
		this.source = source;
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public String getTxtEnvSourceTarget() {
		return txtEnvSourceTarget;
	}

	public void setTxtEnvSourceTarget(String txtEnvSourceTarget) {
		this.txtEnvSourceTarget = txtEnvSourceTarget;
	}

	public String getTxtEnvTarget() {
		return txtEnvTarget;
	}

	public void setTxtEnvTarget(String txtEnvTarget) {
		this.txtEnvTarget = txtEnvTarget;
	}

	public String getTxtEnvSource() {
		return txtEnvSource;
	}

	public void setTxtEnvSource(String txtEnvSource) {
		this.txtEnvSource = txtEnvSource;
	}

	public String getTxtTargetSource() {
		return txtTargetSource;
	}

	public void setTxtTargetSource(String txtTargetSource) {
		this.txtTargetSource = txtTargetSource;
	}

	public String getTxtTarget() {
		return txtTarget;
	}

	public void setTxtTarget(String txtTarget) {
		this.txtTarget = txtTarget;
	}

	public String getTxtSource() {
		return txtSource;
	}

	public void setTxtSource(String txtSource) {
		this.txtSource = txtSource;
	}
	/**
	 * Esemeny elsutese
	 * Kezeli a forras, celpont uzeneteit, es athiv, hogy a szobakat is lekezelje
	 */
	public void fire() {
		Holder sourceEnv = null;
		Holder targetEnv = null;
		if(source!=null) {
			sourceEnv = source.getTopEnvironment();
		}
		if(target!=null) {
			targetEnv = target.getTopEnvironment();
		}
		if((target!=null)&& (source!=null)) {
			if(target instanceof Living) {
				String msg = getTxtTargetSource();
				if(StringUtils.isNotBlank(msg)) {
					String sName = source.getShortNameString();
					if(sName!=null) {
						msg = msg.replace("%S", sName);
					}
					((Living)target).tell(StringUtils.capitalize(msg));
				}
			}
			if(source instanceof Living) {
				String msg = getTxtSourceTarget();
				if(StringUtils.isNotBlank(msg)) {
					String tName = target.getShortNameTargy();
					if(StringUtils.isBlank(tName))
						tName = target.getShortNameString()+"-t";
					if(tName!=null) {
						msg = msg.replace("%T", tName);
					}
					((Living)source).tell(StringUtils.capitalize(msg));
				}
			}
		}
		else {
			if((target!=null) && (target instanceof Living)) {
				String msg = getTxtTarget();
				if(StringUtils.isNotBlank(msg)) {
					((Living)target).tell(StringUtils.capitalize(msg));
				}
				
			}
			if((source!=null) && (source instanceof Living)) {
				String msg = getTxtSource();
				if(StringUtils.isNotBlank(msg)) {
					((Living)source).tell(StringUtils.capitalize(msg));
				}
			}
		}
		if((sourceEnv!=null) && (sourceEnv instanceof Room)) {
			handleRoomEvent((Room)sourceEnv, true, sourceEnv==targetEnv);
		}
		if((targetEnv!=null) && (targetEnv!=sourceEnv) && (targetEnv instanceof Room)) {
			handleRoomEvent((Room)targetEnv, false, true);
		}

	}
	/**
	 * Esemeny ellovese a szobaban.
	 * Mind a forras, mind a cel szobajaban (ha definialtak es kulonbozoek) minden szobaban levon ellenorzi, 
	 * hogy erzekeli-e az esemenyt es eszerint jeleniti meg az uzeneteket a resztvevoknek
	 * @param room ahol elsult
	 * @param isSource a forras szobaja
	 * @param isTarget a celpont szobaja
	 */
	private void handleRoomEvent(Room room,boolean isSource, boolean isTarget) {
		HashSet<String> types = Defaults.senses;
		HashMap<String,Integer> env = room.getStimulus(types);
		if(LOG.isTraceEnabled()) {
			for(String k: env.keySet()) {
				LOG.trace("k: " + k + " v: " + env.get(k));
			}
		}
		// ketteszedni:
		// esemeny megimerhetosege (ez a test alapjan, ami az event-ben van
		// a resztvevok megismerhetosege, ez minden erzekszerv alapjan.
		
		// mindket objektum ebben a szobaban van
		for(Entity obj: room.getContains()) {
			if(obj instanceof Living) {
				LOG.trace("Handle for obj: " + obj.getFullObjectName() + " types:" + String.join(",", types));
				Living l = (Living)obj;
				if((obj!=source)&&(obj!=target)) {
					ArrayList<Sense> list = l.getSenseByType(types);
					LOG.trace("senses: " + list.size());
					String msg = null;
					if((list!=null)&&(!list.isEmpty())) {
						int result = Perception.test(list, env, tests,Perception.T_ANY);
						LOG.trace("Result: " + result);
						if((result==Perception.SOME_ABOVE) ||(result==Perception.SOME_BELOW)) {
							msg = "Valami történt.";
						}
						if((result==Perception.ALL)) {
							String sName = null;
							String tName = null;
							//source text kitalalasa
							if(isSource) {
								// ha targy es valaki tulajdonaban van
								// ide barmi erzekeles kellene.
								ArrayList<SenseTest> sIntensity = source.getPerceptibility(types);
								int sResult = Perception.test(list, env, sIntensity,Perception.T_ANY);
								switch(sResult) {
								case Perception.SOME_ABOVE:
								case Perception.SOME_BELOW:
									if(source instanceof Living) {
										sName = "valaki";
									}
									else {
										sName = "valami";
									}
									break;
								case Perception.ALL:
									sName = source.getShortNameString();
									break;
								}
							}
							//target text kitallasa
							
							if(isTarget) {
								// ha targy es valaki tulajdonaban van
								ArrayList<SenseTest> tIntensity = target.getPerceptibility(types);
								int tResult = Perception.test(list, env, tIntensity,Perception.T_ANY);
								switch(tResult) {
								case Perception.SOME_ABOVE:
								case Perception.SOME_BELOW:
									if(target instanceof Living) {
										tName = "valakit";
									}
									else {
										tName = "valamit";
									}
									break;
								case Perception.ALL:
									tName = target.getShortNameTargy();
									if(StringUtils.isBlank(tName))
										tName = target.getShortNameString()+"-t";
									break;
								}
							}
							if((sName!=null)&&(tName!=null)){
								msg = getTxtEnvSourceTarget();
							}
							else {
								if(sName!=null) {
									msg = getTxtEnvSource();
								}
								else {
									if(tName!=null) {
										msg = getTxtEnvTarget();
									}
									else{
										msg = getTxtEnv();
									}
								}
							}
							
							if(StringUtils.isNotBlank(msg)) {
								if(sName!=null) {
									msg = msg.replace("%S", sName);
								}
								if(tName!=null) {
									msg = msg.replace("%T", tName);
								}
							}
							
						}
						if(StringUtils.isNotBlank(msg)) {
							l.tell(StringUtils.capitalize(msg));
							l.updatedRoom();
							// szoba leiras frissitese az adott livinghez..
						}
						if(result==Perception.ALL) {
							// callback kezelese pl a dalmagiahoz
						}
					}
				}
			}
		}
		
	}

	public String getTxtEnv() {
		return txtEnv;
	}

	public void setTxtEnv(String txtEnv) {
		this.txtEnv = txtEnv;
	}

	public String getTxtSourceTarget() {
		return txtSourceTarget;
	}

	public void setTxtSourceTarget(String txtSourceTarget) {
		this.txtSourceTarget = txtSourceTarget;
	}
	private Event() {
		super();
		tests = new ArrayList<>();
	}
	public void addTest(SenseTest t) {
		tests.add(t);
	}
	/**
	 * Egyszeru event, aminek csak forrasa van, celpontja nincs
	 * @param type	milyen erzekszervvel lehet eszrevenni
	 * @param intensity esemeny erossege
	 * @param source	forras, ami kivaltotta
	 * @param txtEnvSource a forras szobajaban megjelenitendo szoveg
	 * @param txtEnv szoveg, ha a forras nem eszlelheto
	 * @param txtSource a forrasnak megjelenitendo szoveg
	 * @return a kesz esemeny
	 */
	static public Event createSimpleSourceEvent(String type,int intensity, Entity source, String txtEnvSource, String txtEnv, String txtSource) {
		if(StringUtils.isBlank(type)) return null;
		Event ret = new Event();
		SenseTest t = new SenseTest(type, intensity);
		ret.addTest(t);

		ret.setSource(source);
		ret.setTxtEnvSource(txtEnvSource);
		ret.setTxtEnv(txtEnv);
		ret.setTxtSource(txtSource);
		if(LOG.isTraceEnabled()) {
			LOG.trace("Simple created: " + ret.toString());
		}
		return ret;
		
	}
	/**
	 * Egyszeru event, aminek csak forrasa van, celpontja nincs
	 * @param tests erzekszerv-teszt lista az eszrevetelhez
	 * @param source forras, ami kivaltotta
	 * @param txtEnvSource  forras szobajaban megjelenitendo szoveg
	 * @param txtEnv szoveg, ha a forras nem eszlelheto
	 * @param txtSource a forrasnak megjelenitendo szoveg
	 * @return a kesz esemeny
	 */
	static public Event createSimpleSourceEvent(List<SenseTest>tests, Entity source, String txtEnvSource, String txtEnv, String txtSource) {
		Event ret = new Event();
		ret.getTests().addAll(tests);
		ret.setSource(source);
		ret.setTxtEnvSource(txtEnvSource);
		ret.setTxtEnv(txtEnv);
		ret.setTxtSource(txtSource);
		if(LOG.isTraceEnabled()) {
			LOG.trace("Simple created: " + ret.toString());
		}
		return ret;
		
	}
	/**
	 * Sima targyas esemeny letrehozasa, targyakhoz. Celpont nem kap uzenetet.
	 * @param tests erzekszerv-teszt lista az eszrevetelhez
	 * @param source forras, ami kivaltotta
	 * @param subject celpont
	 * @param txtEnvSourceTarget szobaban megjelenitendo szoveg
	 * @param txtEnvTarget szobaban megjelenitendo, ha forras nem lathato
	 * @param txtEnvSource szobaban megjelenitendo, ha a celpont nem lathato
	 * @param txtEnv szobaban megjelenitendo, ha a celpont/forras nem lathato
	 * @param txtSource a forrasnak megjelenitendo szoveg.
	 * @return
	 */
	static public Event createSimpleSourceSubjectEvent(List<SenseTest>tests, Entity source, Entity subject, String txtEnvSourceTarget, String txtEnvTarget, String txtEnvSource, String txtEnv, String txtSource) {
		Event ret = new Event();
		ret.getTests().addAll(tests);
		ret.setSource(source);
		ret.setTarget(subject);
		ret.setTxtEnvSource(txtEnvSource);
		ret.setTxtEnv(txtEnv);
		ret.setTxtSourceTarget(txtSource);
		ret.setTxtEnvSourceTarget(txtEnvSourceTarget);
		ret.setTxtEnvTarget(txtEnvTarget);
		if(LOG.isTraceEnabled()) {
			LOG.trace("Simple subject created: " + ret.toString());
		}
		return ret;
		
	}
	
	/**
	 * Egyszeru event, aminek csak forrasa van, celpontja nincs
	 * Stimulusbol van keszitve
	 * Csak a forras szobajaban megjelenitendo szoveg van kitoltve
	 * 
	 * @param st amibol kesziteni kell
	 * @param source forras
	 * @return
	 */
	static public Event createFromStimulus(Stimulus st, Entity source) {
		Event ret = new Event();
		ret.setSource(source);
		ret.setTxtEnvSource(st.getDescr(source));
		String type = st.getType();
		SenseTest test = new SenseTest(type, st.getIntensity());
		ret.addTest(test);
		if(LOG.isTraceEnabled()) {
			LOG.trace("Simple created from Stimulus: " + st.toString());
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for(Test t:tests) {
			buff.append("{");
			buff.append(t.toString());
			buff.append("}");
		}
		return "{" + buff.toString()+" testType: " + testType+ " source: " + source.getFullObjectName() + " txtEnvSource: " + txtEnvSource + " txtEnv:" + txtEnv + " txtSource: " + txtSource + "}";
	}

	public ArrayList<SenseTest> getTests() {
		return tests;
	}

	public void setTests(ArrayList<SenseTest> tests) {
		this.tests = tests;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}
	


}
