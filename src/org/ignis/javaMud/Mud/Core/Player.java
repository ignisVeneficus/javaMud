package org.ignis.javaMud.Mud.Core;

import java.util.HashMap;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Interface.User;
import org.ignis.javaMud.Mud.action.Action;
import org.ignis.javaMud.Mud.dataholder.InflectionWord;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.messages.Event;
import org.ignis.javaMud.messages.Message;
import org.ignis.javaMud.utils.Colorize;
/**
 * Jatekos karakter
 * @author Ignis
 *
 */
// egyenlore szet van ganyolva
// teszteles celjabol..
public class Player extends Living implements Singleton{
	static private Logger LOG = LogManager.getLogger(Player.class);
	
	static public final String GHOST_SHORT = " szelleme";
	static public final String GHOST_SHORT_T = " szellemét";
	
	
	/**
	 * Rakapcsolt felhasznalo -> socket kezeles
	 */
	@XmlTransient
	private User user;
	/**
	 * szellem
	 */
	@XmlTransient
	private boolean ghost;
	/**
	 * pihen
	 */
	@XmlTransient
	private boolean resting;
	/**
	 * Alias-ok, parancsroviditesek
	 */
	private HashMap<String,String> alias;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public Player() {
		alias = new HashMap<>();
		ghost = false;
		resting=false;
	}

	@Override
	public void tell(String what) {
		super.tell(what);
		if(user!=null) {
			Message msg = new Message();
			LOG.trace(what);
			String[] lines = what.split("\\\n",-1);
			LOG.trace(lines.length);
			for(int i=0;i<lines.length;i++) {
				lines[i] = Colorize.colorize(lines[i]);
			}
			org.ignis.javaMud.messages.Event e = new Event();
			e.setText(lines);
			msg.setEvent(e);
			user.sendToUser(msg);
		}
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		// kezzel belebarmolva, amig nem lesz rendes generalo
		
		Sense s = new Sense("látás",Perception.TYPE_REFLECTION,50,30,Defaults.Sense_Latas);
		addSense(s);
		
		s = new Sense("hallás",Perception.TYPE_SOURCE,50,15,Defaults.Sense_Hallas);
		addSense(s);
		
		s = new Sense("szaglás",Perception.TYPE_SOURCE,50,15,Defaults.Sense_Szaglas);
		addSense(s);
		
		s = new Sense("mágia",Perception.TYPE_INDIVIDUAL,90,7,Defaults.Sense_Magia);
		addSense(s);
		
		
		alias.put("e", "észak");
		alias.put("ek", "északkelet");
		alias.put("k", "kelet");
		alias.put("dk", "délkelet");
		alias.put("d", "dél");
		alias.put("dny", "délnyugat");
		alias.put("ny", "nyugat");
		alias.put("eny", "északnyugat");
		alias.put("n", "néz");
		alias.put("l","leltár");
		
		alias.put("gtemplom", "goto @Fovaros/fovaros_1_83");
	}

	public void processText(String message) {
		String begin = Action.getCommand(message);
		if("".equals(begin)) return;
		LOG.trace(begin);
		String a;
		while((a=alias.get(begin))!=null) {
			LOG.trace(a);
			message = StringUtils.replaceOnce(message,begin, a);
			LOG.trace(message);
			begin = Action.getCommand(message);
		}
		super.processText(message);		
	}

	@Override
	protected void _destr() {
		super._destr();
		// save adatbazisba
		// user fieldekbe, player 1 db xmlbe 
	}
	
	public void beforeMarshal(Marshaller marshaller) {
		// minden valtozot ami olyan, szepen osszecsomagolni, pl az aliast
		// meghivni az ost (living) es ott is, pl a content-et
		
	}

	@Override
	public void tellStatus() {
		//            0        1         2         3         4         5         6         7
		//            1234567890123456789012345678901234567890123456789012345678901234567890123456789
		//           "  HP: 1234 [##########]  |  SP: 1234 [##########]  |  MP: 1234 [##########] "+
		double hpP = (double)getActHP() / getMaxHP();
		LOG.trace("hp: " +getActHP() + "/" + getMaxHP() + "=>" + hpP );
		double spP = (double)getActSP() / getMaxSP();
		double mpP = 0;
		if(getMaxMP()>0) {
			mpP = (double)getActMP() / getMaxMP();
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append(" ");
		buff.append(Colorize.C_B_RED);
		buff.append("HP:");
		buff.append(Colorize.RESET);
		buff.append(" ");
		buff.append(colorize(hpP));
		buff.append(String.format("%5d", getActHP()));
		buff.append(Colorize.RESET);
		buff.append(" [");
		buff.append(colorize(hpP));
		buff.append(StringUtil.drawLine(hpP,10));
		buff.append(Colorize.RESET);
		buff.append("] | ");
		
		buff.append(Colorize.C_B_GREEN);
		buff.append("SP:");
		buff.append(Colorize.RESET);
		buff.append(" ");
		buff.append(colorize(spP));
		buff.append(String.format("%5d", getActSP()));
		buff.append(Colorize.RESET);
		buff.append(" [");
		buff.append(colorize(spP));
		buff.append(StringUtil.drawLine(spP,10));
		buff.append(Colorize.RESET);
		buff.append("] ");
		
		if(getMaxMP()>0) {
			buff.append("| ");
			buff.append(Colorize.C_B_CYAN);
			buff.append("MP:");
			buff.append(Colorize.RESET);
			buff.append(" ");
			buff.append(colorize(mpP));
			buff.append(String.format("%5d", getActMP()));
			buff.append(Colorize.RESET);
			buff.append(" [");
			buff.append(colorize(mpP));
			buff.append(StringUtil.drawLine(mpP,10));
			buff.append(Colorize.RESET);
			buff.append("]");
		}
		
		tell(buff.toString());
		
		if(user!=null) {
			user.sendStatus();
		}
	}
	
	private String colorize(double percent) {
		if(percent<0.4) return Colorize.C_B_RED;
		if(percent<0.8) return Colorize.C_YELLOW;
		return Colorize.C_B_GREEN;
	}
	
	@Override
	public void _die() {
		ghost = true;
	}

	@Override
	public void setEnvironment(Holder environment) {
		super.setEnvironment(environment);
		if((environment!=null) && (user!=null)) {
			user.sendRoom();
		}
	}

	@Override
	public String getDescription(Living obj, boolean longDescr) {
		return super.getDescription(obj, longDescr);
		
		// egyeb leirasok
	}

	@Override
	public boolean hasMaterialForm() {
		return !ghost;
	}

	@Override
	public InflectionWord getShortName() {
		InflectionWord iw = super.getShortName();
		if(ghost) {
			InflectionWord ret = new InflectionWord();
			ret.setWord(iw.getWord() + GHOST_SHORT);
			ret.setTargy(iw.getWord() + GHOST_SHORT_T);
			return ret;
		}
		return iw;
	}

	@Override
	public boolean canMove() {
		if(super.canMove()) {
			if(resting) {
				tell("Nem teheted. Pihensz. A "+Defaults.Color_command +"Felkel" + Colorize.RESET +" megszakítja");
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if(resting) {
			tell("Pihengetsz...");
			tellStatus();
			if((getActHP() == getMaxHP()) &&
					(getActSP() == getMaxSP()) &&
					(getActMP() == getMaxMP())) {
				tell("Kipihented magad.");
				processText("felkel");
			}
		}
	}

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public boolean isResting() {
		return resting;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
	}

	@Override
	public int getActHPReg() {
		int ret= super.getActHPReg();
		if(isResting()) {
			ret += Defaults.RestMultiple;
		}
		return ret;
	}

	@Override
	public int getActMPReg() {
		int ret=  super.getActMPReg();
		if(isResting()) {
			ret += Defaults.RestMultiple;
		}
		return ret;
	}

	@Override
	public int getActSPReg() {
		int ret=  super.getActSPReg();
		if(isResting()) {
			ret += Defaults.RestMultiple;
		}
		return ret;
	}

	public HashMap<String, String> getAlias() {
		return alias;
	}

	private void _addSP(int sp) {
		super.addSP(sp);
	}

	private void _addHP(int hp) {
		super.addHP(hp);
	}

	private void _addMP(int mp) {
		super.addMP(mp);
	}
	
	
	@Override
	public void addSP(int sp) {
		_addSP(sp);
		if(user!=null) {
			user.sendStatus();
		}
	}

	@Override
	public void addHP(int hp) {
		_addHP(hp);
		if(user!=null) {
			user.sendStatus();
		}
	}

	@Override
	public void addMP(int mp) {
		_addMP(mp);
		if(user!=null) {
			user.sendStatus();
		}
	}

	@Override
	public void minusHp(int hPLost, String why) {
		super.minusHp(hPLost, why);
	}

	@Override
	public void updatedRoom() {
		if(user!=null) {
			user.sendRoom();
		}
	}
	@Override
	public void updatedLiving() {
		if(user!=null) {
			user.sendStatus();
		}
	}
	
	

	@Override
	public void triggerEntry(Living obj) {
		// TODO Auto-generated method stub
		
	}



}
