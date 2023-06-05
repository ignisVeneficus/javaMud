package org.ignis.javaMud.Mud.AreaEffect;

import org.ignis.javaMud.Mud.Callables.ObjectCallable;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Event;
import org.ignis.javaMud.Mud.Core.Living;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.data.AreaEffect;
import org.ignis.javaMud.Mud.dataholder.Test;
import org.ignis.javaMud.Mud.dataholder.test.SkillTest;
import org.ignis.javaMud.Mud.utils.Defaults;

public class Water {
	private static ObjectCallable<Boolean,Entity> success;
	private static ObjectCallable<Boolean,Entity> failed;
	private static ObjectCallable<Boolean,Entity> check;
	private static Test test;
	
	static public void swim(Entity ent) {
		if(!(ent instanceof Living)) return;

		Living l = (Living)ent;
		int SpLost = Defaults.getSwimSPLost();
		if(l.getActSP()<SpLost) {
			Event e = Event.createSimpleSourceEvent(Defaults.getDefaultTestFor(Defaults.Sense_Hallas + "," + Defaults.Sense_Latas), l, 
					"%S fáradtan úszni próbál de fuldokás lett belőle", "", "Fáradt vagy az uszáshoz. Fuldoklasz.");
			e.fire();
			l.minusHp(Defaults.getDrownHPLost(), "fulladás");
			return;
		}
		Event e = Event.createSimpleSourceEvent(Defaults.getDefaultTestFor(Defaults.Sense_Hallas + "," + Defaults.Sense_Latas), l, 
				"%S úszik", "", "Úszol.");
		e.fire();
		l.addSP(-1* SpLost);
		return;
		
	}
	static public void drown(Entity ent) {
		if(!(ent instanceof Living)) return;

		Living l = (Living)ent;
		Event e = Event.createSimpleSourceEvent(Defaults.getDefaultTestFor(Defaults.Sense_Hallas + "," + Defaults.Sense_Latas), l, 
				"%S kapálodzik, hogy fentmaradjon a víz felett de fuldokás lett belőle", "", "Kapálodzol, hogy fentmaradj de nem sikerül. Fuldoklasz.");
		e.fire();
		l.minusHp(Defaults.getDrownHPLost(), "fulladás");
	}
	static public boolean check(Entity e) {
		if(!(e instanceof Living)) return false;

		Living l = (Living)e;
		
		return l.hasMaterialForm();
	}

	public static final AreaEffect createWater() {
		AreaEffect af = new AreaEffect();
		af.setName("viz");
		af.setIntervall(3);
		af.setRemainderCircle(0);
		if(test == null) {
			test = new SkillTest("uszas", 10);
		}
		af.setTest(test);
		if(success==null) {
			success = new ObjectCallable<Boolean,Entity>() {
				public Boolean call(Entity obj) {
					swim(obj);
					return false;
				}
			};
		}
		af.setSuccess(success);
		if(failed==null) {
			failed = new ObjectCallable<Boolean,Entity>() {
				public Boolean call(Entity obj) {
					drown(obj);
					return false;
				}
			};
		}
		af.setFailed(failed);
		if(check==null) {
			check=new ObjectCallable<Boolean,Entity>() {
				public Boolean call(Entity obj) {
					return check(obj);
				}
			};
		}
		af.setCheckObj(check);
		return af;
	}
	
}
