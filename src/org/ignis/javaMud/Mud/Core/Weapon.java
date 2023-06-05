package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.Colorize;
/**
 * Fegyverek
 * @author Ignis
 *
 */
@XmlRootElement(name ="Weapon")
@XmlAccessorType(XmlAccessType.FIELD)
public class Weapon extends Item implements Usable {
	
	/**
	 * A fegyver tipusa, a panceloknal es a sebzes kiirasanal szukseges
	 */
	@XmlElement(name="weaponType")
	private Set<String> weaponType;

	/**
	 * Bonus lista, XML-bol csak ezzel lehet a stat-okhoz, sense-hez hozzaferni.
	 */
	@XmlElement(name = "bonus")
	private List<Bonus> xmlBonus;

	/**
	 * JS fuggveny neve, ami a megfogasnal lefut
	 */
	@XmlElement(name="equip")
	private String jsEquip;
	
	/**
	 * Js fuggveny neve, ami az eltesz eseten fut le
	 */
	@XmlElement(name="unEquip")
	private String jsUnEquip;

	/**
	 * JS fuggveny neve, ami a megfog elott, ellenorzesnek fut le
	 */
	@XmlElement(name="canEquip")
	private String jsCanEquip;

	/**
	 * JS fuggveny neve, ami az eltesz elott, ellenorzesnek fut le
	 */
	@XmlElement(name="canUnEquip")
	private String jsCanUnEquip;
	
	/**
	 * Sebzes, Kockadobas tipus is elfogadott
	 */
	@XmlElement(name="damage")
	private String damage;
	
	/**
	 * hany kezes a fegyver
	 */
	@XmlElement(name="nrLimbs")
	private Integer nrLimbs;
	
	/**
	 * Tamadoerteke
	 */
	@XmlElement(name="AC")
	private Integer ac;
	
	@XmlTransient
	private boolean hold;
	@XmlTransient
	private ArrayList<String> limbs;
	
	/**
	 * Eltesz, de itt csak vegrehajtodik, mar nincsenek esemenyek
	 */
	public void _unHold() {
		Holder env = getEnvironment();
		if(env instanceof Living) {
			Living liv = (Living)env;
			checkAndCallJsFunction(jsUnEquip);
			liv.removeBonusBySource(this);
			if(StringUtils.isNotBlank(jsUnEquip)) {
				if(hasJsFunction(jsUnEquip)) {
					callJsFunction(jsUnEquip);
				}
			}
			liv.releasekHoldingLimbs(limbs);
			hold = false;
		}
		
	}
	
	/**
	 * Megfog, de itt csak vegrehajtodik, mar nincsenek esemenyek
	 */
	public void _hold() {
		Holder env = getEnvironment();
		if(env instanceof Living) {
			Living liv = (Living)env;
			List<String> limbs = liv.lockHoldingLimbs(nrLimbs);
			if((limbs==null)||(limbs.size()<nrLimbs)){
				// hiba
				return;
			}
			checkAndCallJsFunction(jsEquip);
			liv.addBonuses(xmlBonus);
			if(StringUtils.isNotBlank(jsEquip)) {
				if(hasJsFunction(jsEquip)) {
					callJsFunction(jsEquip);
				}
			}
			hold=true;
			
		}
	}
	/**
	 * Megfog, de lefutnak az esemenyek
	 */
	
	public ReturnType hold() {
		Holder env = getEnvironment();
		if(env instanceof Living) {
			ReturnType type = canUse();
			// error
			if(type==null) return ReturnType.success();
			
			if(!type.isSuccess()) {
				return type;
			}
			
			SenseTest t = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
			ArrayList<SenseTest> set = new ArrayList<>();
			set.add(t);
			
			Event e = Event.createSimpleSourceSubjectEvent(set, (Living)env, this,
					"%S megfogja %T.",
					null,
					"%S matat valamit",
					null,
					"Megfogod a %T.");
			if(e!=null) {
				e.fire();
			}
			_hold();
			return ReturnType.success();
		}
		return ReturnType.failed();
	}
	/**
	 * Eltesz, de lefutnak az esemenyek
	 */
	public ReturnType unHold() {
		Holder env = getEnvironment();
		if(env instanceof Living) {
			ReturnType type = canUnUse();
			// error
			if(type==null) return ReturnType.success();
			
			if(!type.isSuccess()) {
				return type;
			}

			SenseTest t = new SenseTest(Defaults.Sense_Latas, Defaults.getDefaultIntensity(Defaults.Sense_Latas));
			ArrayList<SenseTest> set = new ArrayList<>();
			set.add(t);
			
			Event e = Event.createSimpleSourceSubjectEvent(set, (Living)env, this,
					"%S elteszi a %T.",
					null,
					"%S matat valamit",
					null,
					"Elteszed a %T.");
			if(e!=null) {
				e.fire();
			}
			_unHold();
			return ReturnType.success();
		}
		return ReturnType.failed();

	}
	@Override
	public void use() {
		hold();
	}

	@Override
	public void unUse() {
		unHold();
	}

	public Weapon() {
		super();
	}

	
	
	@Override
	protected void _destr() {
		_unHold();
		super._destr();
	}

	@Override
	public ReturnType canDrop() {
		if(hold) {
			return ReturnType.failed("Fogod! Előtte tedd el az "+Defaults.Color_command+"eltesz" + Colorize.RESET + " paranccsal!");
		}
		if(StringUtils.isNotBlank(jsCanUnEquip)) {
			if(hasJsFunction(jsCanUnEquip)) {
				java.lang.Object ret = callJsFunction(jsCanUnEquip);
				if(ret!=null) {
					return ReturnType.failed(ret.toString());
				}
			}
		}		
		return super.canDrop();
	}
	public ReturnType canUse() {
		Holder env = getEnvironment();
		if(!(env instanceof Living)) {
			return ReturnType.failed();
		}
		Living liv = (Living)env;
		if(!liv.hasHoldingLimbs(nrLimbs)) {
			return ReturnType.failed("Nincs elég szabad végtagod");
		}
		
		if(StringUtils.isNotBlank(jsCanEquip)) {
			if(hasJsFunction(jsCanEquip)) {
				java.lang.Object ret = callJsFunction(jsCanEquip);
				if(ret!=null) {
					return ReturnType.failed(ret.toString());
				}
			}
		}
		return ReturnType.success();
		
	}
	
	public ReturnType canUnUse() {
		if(StringUtils.isNotBlank(jsCanUnEquip)) {
			if(hasJsFunction(jsCanUnEquip)) {
				java.lang.Object ret = callJsFunction(jsCanUnEquip);
				if(ret!=null) {
					return ReturnType.failed(ret.toString());
				}
			}
		}
		return ReturnType.success();
		
	}

	@Override
	public void moveObject(Holder where) {
		if(hold) {
			unHold();
		}
		super.moveObject(where);
	}

	@Override
	protected void init() {
		super.init();
		if((nrLimbs == null) || (nrLimbs<1))
			nrLimbs =1;
		if((ac == null) || (ac<1))
			ac =1;
		if(xmlBonus!=null) {
			for(Bonus b:xmlBonus) {
				b.setSource(this);
			}
		}
	}

	@Override
	public String getShortNameString() {
		String extra = "";
		if(isHold()) {
			String where = StringUtil.listToString(limbs);
			extra = " megfogva: " +where;
		}
		return super.getShortNameString() + extra;
	}

	public boolean isHold() {
		return hold;
	}

	@Override
	public boolean isUsed() {
		return isHold();
	}

	@Override
	public void _unUse() {
		_unHold();
		
	}
	
	
}
