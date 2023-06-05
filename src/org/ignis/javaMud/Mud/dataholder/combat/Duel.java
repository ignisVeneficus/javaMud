package org.ignis.javaMud.Mud.dataholder.combat;

import org.ignis.javaMud.Mud.Core.Living;
/**
 * A harcban ket harcolo felet tartalmaz.
 * Amennyiben a ket fel nem azonos helyszinen van, elkezd egy timeout leporigni, amennyiben elfogyott, a ket fel mar nem harcol
 * egymassal, akkor sem, ha egy helyen lesznek.
 */
public class Duel {
	private Living one;
	private Living two;
	private int timeout;
	private int startTimeout;
	private Duel(Living one, Living two, int timeout) {
		super();
		this.one = one;
		this.two = two;
		this.startTimeout = timeout;
		this.timeout = timeout;
	}
	public boolean has(Living name) {
		if(name!=null && one!=null && two !=null) {
			return name==one || name==two;
		}
		else {
			timeout = 0;
			return false;
		}
	}
	public void tick() {
		if(timeout>0) {
			if(one.getEnvironment() == two.getEnvironment()) {
				reset();
			}
			else {
				timeout--;
			}
		}
	}
	public void reset() {
		timeout = startTimeout;
	}
	public boolean isLiving() {
		return timeout>0;
	}
	public Living getOne() {
		return one;
	}
	public Living getTwo() {
		return two;
	}
	
}
