package org.ignis.javaMud.Mud.Core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.ignis.javaMud.utils.xml.DiceIntegerAdapter;
/**
 * Kaja, amit meg lehet enni -> gyogyit
 * @author Ignis
 *
 */
@XmlRootElement(name ="Food")
public class Food extends Item {
	/**
	 * Mennyi HP-t gyogyit
	 */
	@XmlAttribute(name = "HP")
	@XmlJavaTypeAdapter(DiceIntegerAdapter.class)
	private Integer Hp;
	/**
	 * Maradvany, ami a ennivalo elfogyasztasa utan marad.
	 * mint path
	 */
	@XmlElement
	private String remainder;
	
	public Food() {
		super();
	}
	@Override
	protected void init() {
		super.init();
		if(Hp==null) {
			Hp = 0;
		}
		if(Hp<0) {
			Hp = 0;
		}
	}
	public int getHp() {
		return Hp;
	}
	public void setHp(int hp) {
		Hp = hp;
	}
	public String getRemainder() {
		return remainder;
	}
	
	
}
