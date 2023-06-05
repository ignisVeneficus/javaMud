package org.ignis.javaMud.Mud.dataholder.monster;

import java.util.concurrent.ThreadLocalRandom;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.ignis.javaMud.Mud.Core.Living;
/**
 * Szornyek eltunese, pl "beballag az erdoben"
 * Esemeny generalodik
 * @author Ignis
 *
 */
public class Disappear extends Appear{
	/**
	 * minimum ido, amikor bekovetkezik
	 */
	@XmlAttribute(name="minTime")
	private int minTime;
	/**
	 * Maximum ido amikor bekovetkezik
	 */
	@XmlAttribute(name="maxTime")
	private int maxTime;
	
	@XmlTransient
	private int tick;
	
	@XmlTransient
	private int maxTick;
	
	public void init(Living l) {
		super.init(l);
		maxTick = ThreadLocalRandom.current().nextInt(minTime, maxTime);
		tick = 0;
	}
	
}
