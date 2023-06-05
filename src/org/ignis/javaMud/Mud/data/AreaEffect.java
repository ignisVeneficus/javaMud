package org.ignis.javaMud.Mud.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Callables.Callable;
import org.ignis.javaMud.Mud.Callables.ObjectCallable;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.dataholder.Test;

public class AreaEffect {
	static private Logger LOG = LogManager.getLogger(AreaEffect.class);
	/**
	 * Nev tudjuk mi az
	 */
	private String name;
	/**
	 * hany tick-enkent hivodik
	 */
	private int intervall;
	/**
	 * tick szamlalo
	 */
	private int tickNr;
	/**
	 * Teszt, ami eldonti sikeres, vagy sem
	 */
	private Test test;
	/**
	 * fuggveny siker esetere
	 */
	private ObjectCallable<Boolean,Entity> success;
	/**
	 * fuggveny nem siker esetere
	 */
	private ObjectCallable<Boolean,Entity> failed;
	
	/**
	 * lejaratos fuggveny
	 */
	private Callable<Boolean> end;
	/**
	 * visszamaradt ciklus
	 */
	private int remainderCircle;
	/**
	 * ellenorzi
	 */
	private ObjectCallable<Boolean,Entity> checkObj;
	
	/**
	 * Tick kezelese, szamolja a tick-eket, meghivja a fuggvenyeket, stb.
	 * @return true, ha mar lefutott az effekt es meg kell szuntetni
	 */
	public boolean tick(Entity[] objs) {
		LOG.trace("Tick => " + objs.length + " nr: " + tickNr + " % " + intervall);
		synchronized (this) {
			tickNr++;
			if((tickNr=(tickNr%intervall)) == 0) {
				for(Entity obj: objs) {
					LOG.trace(obj.getFullObjectName());
					if(obj instanceof Living) {
						Living liv = (Living)obj;
						if(checkObj!=null) {
							boolean ok = checkObj.call(obj);
							if(!ok) continue;
						}
						boolean s = true;
						if(test!=null) {
							s = test.doTest(liv);
						}
						if(s) {
							if(success!=null) success.call(liv);
						}
						else {
							if(failed!=null) failed.call(liv);
						}
					}
				}
				if(remainderCircle>0) {
					remainderCircle--;
					if(remainderCircle==0) {
						if(end!=null) end.call();
						return true;
					}
				}
				
			}
		}
		return false;
	}
	public AreaEffect() {
		tickNr = 0;
		intervall =1;
		remainderCircle =0;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIntervall() {
		return intervall;
	}
	public void setIntervall(int intervall) {
		if(intervall <1 ) throw new IllegalArgumentException("Intervall must be greater than 0");
		this.intervall = intervall;
	}
	public Test getTest() {
		return test;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	public ObjectCallable<Boolean,Entity> getSuccess() {
		return success;
	}
	public void setSuccess(ObjectCallable<Boolean,Entity> success) {
		this.success = success;
	}
	public ObjectCallable<Boolean,Entity> getFailed() {
		return failed;
	}
	public void setFailed(ObjectCallable<Boolean,Entity> failed) {
		this.failed = failed;
	}
	public Callable<Boolean> getEnd() {
		return end;
	}
	public void setEnd(Callable<Boolean> end) {
		this.end = end;
	}
	public int getRemainderCircle() {
		return remainderCircle;
	}
	public void setRemainderCircle(int remainderCircle) {
		this.remainderCircle = remainderCircle;
	}
	public ObjectCallable<Boolean,Entity> getCheckObj() {
		return checkObj;
	}
	public void setCheckObj(ObjectCallable<Boolean,Entity> checkObj) {
		this.checkObj = checkObj;
	}
}
